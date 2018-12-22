/**
 * This Lambda function is an example of an OpenTracing-instrumented Roulette
 * game using the Jaeger Java tracer where requests containing a chosen number made
 * to an API Gateway resource are compared to a random number from the wheel.
 *
 * GET /my_resource/?choice=36 -> 404 Loss
 * GET /my_resource/?choice=00 -> 200 Win
 *
 * Winning requests will produce a logged error.  You can trigger a winning event by
 * setting a "win" query parameter with an arbitrary value.
 *
 * GET /my_resource/?win=true
 */
package example;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.net.HttpURLConnection;

import org.json.JSONObject;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import io.jaegertracing.internal.samplers.ConstSampler;
import io.jaegertracing.zipkin.ZipkinV2Reporter;
import io.opentracing.Tracer;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.log.Fields;
import io.opentracing.tag.Tags;
import okhttp3.Request;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.okhttp3.OkHttpSender;

// This example is for an API Gateway-proxied Lambda function.  Use your custom event parameters where needed.
public class RoulettePlayer implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    // Roulette wheel address to position mapping, helpful for "00"
    private static final String[] numToChoice = initNumToChoice();
    private static final Map<String, Integer> choiceToNum = initChoiceToNum();

    private static final String[] initNumToChoice() {
        String[] numToChoice = new String[38];
        numToChoice[37] = "00";
        for (int i = 0; i < 37; i++) {
            numToChoice[i] = Integer.toString(i);
        }
        return numToChoice;
    }

    private static final Map<String, Integer> initChoiceToNum() {
        Map<String, Integer> choiceToNum = new HashMap();
        for (int i = 0; i < 38; i++) {
            choiceToNum.put(numToChoice[i], i);
        }
        return choiceToNum;
    }

    private Tracer tracer;

    /**
     * It's expected that there will be a single instance of an opentracing.Tracer per process,
     * and it's a best practice to register it with io.opentracing.util.GlobalTracer.
     * However, since no RequestHandler will ever field more than one request at a time in Lambda
     * we will cycle through io.opentracing.Tracer member instances to allow forcing a span flush
     * at the end of each request.
     */
    private static io.opentracing.Tracer initTracer() {
        String ingestUrl = System.getenv("SIGNALFX_INGEST_URL");
        String accessToken = System.getenv("SIGNALFX_ACCESS_TOKEN");
        if (ingestUrl == null) {
            if (accessToken == null) {
                throw new RuntimeException(
                        "You must set the SIGNALFX_ACCESS_TOKEN Lambda environment variable to be your token " +
                                "if you don't set SIGNALFX_INGEST_URL to point to your Gateway."
                );
            }
            ingestUrl = "https://ingest.signalfx.com/v1/trace";
        }

        // Build the sender that does the HTTP request containing spans to our ingest server.
        OkHttpSender.Builder senderBuilder = OkHttpSender.newBuilder()
                .compressionEnabled(true)
                .endpoint(ingestUrl);

        // Add an interceptor to inject the SignalFx X-SF-Token auth header.
        senderBuilder.clientBuilder().addInterceptor(chain -> {
            Request.Builder builder = chain.request().newBuilder();
            if (accessToken != null) {
                builder.addHeader("X-SF-Token", accessToken);
            }
            Request request = builder.build();
            return chain.proceed(request);
        });

        OkHttpSender sender = senderBuilder.build();

        Tracer tracer = new io.jaegertracing.Configuration("signalfx-lambda-java-example")
                // We need to get a builder so that we can directly inject the reporter instance.
                .getTracerBuilder()
                // This configures the tracer to send all spans, but you will probably want to use
                // something less verbose like a ProbabilisticSampler
                .withSampler(new ConstSampler(true))
                // Configure the tracer to send spans in the Zipkin V2 JSON format instead of the
                // default Jaeger UDP protocol, which we do not support.
                .withReporter(new ZipkinV2Reporter(AsyncReporter.create(sender)))
                .build();

        return tracer;
    }

    @Override // Our registered Lambda handler entrypoint (example.RoulettePlayer::handleRequest)
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        // Create a Tracer for the lifetime of the request
        this.tracer = initTracer();
        // Create and start an active root span that will finish() when the Scope resouce is closed
        try (Scope scope = tracer.buildSpan("handleRequest").startActive(true)) {
            Span span = scope.span();

            // Use OpenTracing tags to denote request-level information
            Tags.HTTP_METHOD.set(span, requestEvent.getHttpMethod());
            Tags.HTTP_URL.set(span, requestEvent.getPath());

            // Retrieve any execution context information and tag for future
            // debugging or analytics:
            // https://docs.aws.amazon.com/lambda/latest/dg/java-context-object.html
            span.setTag("AwsRequestId", context.getAwsRequestId());

            JSONObject responseJSON = new JSONObject();

            // Obtain the user's position choice or generate one if invalid or unprovided
            String choice = getChoice(requestEvent);
            responseJSON.put("Choice", choice);

            // Obtain the current traceId for viewing within Signal Fx
            // This is for demonstration purposes only.
            String traceId = getTraceId(span);
            responseJSON.put("TraceId", traceId);

            int statusCode;
            String result;
            Scope childScope = tracer.buildSpan("playRoulette").asChildOf(span).startActive(true);
            try {
                result = playRoulette(choice);
                responseJSON.put("Result", result);
                statusCode = HttpURLConnection.HTTP_NOT_FOUND;
            } catch (RouletteException e) {
                // Provide span with Exception info
                Tags.ERROR.set(span, true);
                Map<String, Object> logFields = new HashMap();
                logFields.put(Fields.EVENT, "error");
                logFields.put(Fields.ERROR_OBJECT, e);
                logFields.put(Fields.MESSAGE, e.getMessage());
                span.log(logFields);

                result = "You won!";
                responseJSON.put("Result", result);
                statusCode = HttpURLConnection.HTTP_OK;
            } finally {
                childScope.close();
            }

            span.setTag("Result", result);
            Tags.HTTP_STATUS.set(span, statusCode);

            APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent()
                    .withStatusCode(statusCode)
                    .withBody(responseJSON.toString());

            return responseEvent;
        } finally {
            // This is a Jaeger-specific requirement to make sure the Tracer flushes
            // and sends its spans since this is a short lived handler that regenerates
            // the Tracer member variable with every invocation.
            ((io.jaegertracing.internal.JaegerTracer) tracer).close();
        }
    }


    /**
     * getChoice retrieves a user's spin choice from an API Gateway request.
     * If no or an invalid choice is provided in the request, it selects one at random.
     * If a "win" query parameter has been provided, returns "win" to guarantee success.
     */
    private String getChoice(APIGatewayProxyRequestEvent requestEvent) {
        // Retrive the current span from our Tracer's ScopeManager
        // In production, anticipating null return values is advised.
        Span span = tracer.activeSpan();

        Map<String, String> queryParameters = requestEvent.getQueryStringParameters();
        if (queryParameters == null) {
            Map<String, String> logFields = new HashMap();
            logFields.put(Fields.EVENT, "No choice query parameter provided.");
            span.log(logFields);
            queryParameters = new HashMap();
        }

        Boolean winFlag = false;
        String choice;
        if (queryParameters.containsKey("win")) {
            choice = "win";
            winFlag = true;
        } else if (queryParameters.containsKey("choice")
                && choiceToNum.containsKey(queryParameters.get("choice"))) {
            choice = queryParameters.get("choice");
            span.log("Request contains valid choice " + choice);
        } else {
            choice = getRandomPosition();
            span.setTag("randomChoice", choice);
            span.log("Request didn't provide valid choice. Using " + choice + " selected at random.");
        }
        span.setTag("winFlag", winFlag);
        return choice;
    }

    /**
     * Converts a long to Zipkin trace ID format. Per OpenTracing, trace IDs are implementation
     * specific, so this Jaeger interface is not intended for instrumentation and is for
     * ease of demo trace retrieval only.
     */
    private String getTraceId(Span span) {
        long traceId = ((io.jaegertracing.internal.JaegerSpanContext) span.context()).getTraceId();
        String formattedTraceId = String.format("%016x", traceId);
        return formattedTraceId;
    }

    private String getRandomPosition() {
        int position = ThreadLocalRandom.current().nextInt(0, 38);
        return numToChoice[position];
    }

    public String playRoulette(String choice) throws RouletteException {
        Span span = tracer.activeSpan();
        span.setTag("Choice", choice);

        String actual;
        try (Scope childScope = tracer.buildSpan("spinRouletteWheel").asChildOf(span).startActive(true)) {
            actual = spinRouletteWheel();
        }
        span.setTag("Actual", actual);

        if (actual.equals(choice) || choice.equals("win")) {
            throw new RouletteException("Confirmation Bias!");
        }

        return String.format("You Lost! The ball landed on %s.", actual);
    }

    private String spinRouletteWheel() {
        Span span = tracer.activeSpan();

        String position = "0";
        for (int i = 0; i < 1000000; i++) { // simulate meaningful work
            position = getRandomPosition();
        }
        span.setTag("Position", position);

        return position;
    }
}

class RouletteException extends Exception {
    public RouletteException(String s) {
        super(s);
    }
}
