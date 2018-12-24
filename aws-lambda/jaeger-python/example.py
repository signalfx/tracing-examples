# This Lambda function is an example of an OpenTracing-instrumented Roulette
# game using the Jaeger Python tracer where requests containing a chosen number
# made to an API Gateway resource are compared to a random number from the wheel.
#
# GET /my_resource/?choice=36 -> 404 Loss
# GET /my_resource/?choice=00 -> 200 Win
#
# Winning requests will produce a logged error.  You can trigger a winning event by
# setting a "win" query parameter with an arbitrary value.
#
# GET /my_resource/?win=true
#
from random import randint
import traceback
import json
import os
import time

from jaeger_client import Config
from opentracing.ext import tags


# Roulette address/position maps, helpful for "00"
num_to_choice = [str(i) for i in range(37)] + ['00']
choice_to_num = {num_to_choice[i]: i for i in range(38)}


class RouletteError(Exception):

    pass


class RoulettePlayer(object):

    def __init__(self):
        # Though a best practice to have a global, persistent Tracer, because of the
        # asynchronous nature of the Jaeger Python client, in this demo we cycle through a Tracer
        # instance per RequestResponse invocation.  Here we opt not to freeze background span
        # processing for future context thawing and attempt to flush spans before returning
        # from the request handler.
        #
        # If tracing heavily-trafficked APIs, it may be more desirable to implement robust,
        # idempotent Tracer registration and access per AWS Lambda best practices:
        # https://docs.aws.amazon.com/lambda/latest/dg/best-practices.html#function-code
        # https://docs.aws.amazon.com/lambda/latest/dg/running-lambda-code.html
        self.tracer = self.create_tracer()  # Create a tracer for the lifetime of the request

    def handle_request(self, event, context):
        # Create the root span, specifying a span.kind tag in the
        # https://github.com/opentracing/opentracing-python/blob/master/opentracing/ext/tags.py
        span_tags = {tags.SPAN_KIND: tags.SPAN_KIND_RPC_SERVER}
        with self.tracer.start_span('handle_request', tags=span_tags) as span:
            # Use OpenTracing tags to denote request-level information
            span.set_tag(tags.HTTP_METHOD, event['httpMethod'])
            span.set_tag(tags.HTTP_URL, event['path'])

            # Retrieve any execution context information and tag for future
            # debugging or analytics: https://docs.aws.amazon.com/lambda/latest/dg/python-context-object.html
            span.set_tag('aws_request_id', context.aws_request_id)

            choice = self.get_choice(event, span)

            # Per OpenTracing, trace IDs are implementation specific, so this Jaeger interface
            # is not intended for instrumentation and is for ease of demo trace retrieval only.
            trace_id = '{0:x}'.format(span.context.trace_id)

            response_body = dict(choice=choice, trace_id=trace_id)

            with self.tracer.start_span('play_roulette', child_of=span) as child_span:
                try:
                    result = self.play_roulette(choice, child_span)
                    status_code = 404
                except RouletteError as e:
                    child_span.set_tag(tags.ERROR, True)
                    tb = traceback.format_exc()
                    child_span.log_kv({'event': 'error',
                                       'error.kind': str(type(e)),
                                       'error.object': tb})
                    result = 'You Won!'
                    status_code = 200

            span.set_tag('result', result)
            response_body['result'] = result
            span.set_tag(tags.HTTP_STATUS_CODE, status_code)
            response = dict(statusCode=status_code, body=json.dumps(response_body))

        # Known issue with Jaeger's Python client
        # https://github.com/jaegertracing/jaeger-client-python/issues/50
        self.wait_for_flushed_spans()

        return response

    def create_tracer(self):
        access_token = os.getenv('SIGNALFX_ACCESS_TOKEN')
        ingest_url = os.getenv('SIGNALFX_INGEST_URL')

        if ingest_url is None:
            if access_token is None:
                raise Exception("You must set the SIGNALFX_ACCESS_TOKEN Lambda environment variable to be your token "
                                "if you don't set SIGNALFX_INGEST_URL to point to your Gateway.")
            ingest_url = 'https://ingest.signalfx.com/v1/trace'

        # Establish a jaeger-client-python configuration to report spans to SignalFx
        config = {'sampler': {'type': 'const',
                              'param': 1},
                  'jaeger_endpoint': ingest_url,
                  'reporter_flush_interval': .01,
                  'logging': False}  # Report helpful span submission statements and errors to log handler

        if access_token is not None:
            # Required static username for Access Token authentication via Basic Access
            config.update(dict(jaeger_user='auth', jaeger_password=access_token))

        config = Config(
            config=config,
            service_name='signalfx-lambda-python-example',
            validate=True,  # Have jaeger_client fail quickly on invalid configuration
        )

        return config.new_tracer()

    def get_choice(self, event, span):
        """
        Retrieve a user's spin choice from an API Gateway request.
        If no or an invalid choice is provided in the request, it selects one at random.
        If a "win" query parameter has been provided, returns "win" to guarantee success.
        """
        query_parameters = event.get('queryStringParameters') or {}
        win = query_parameters.get('win')
        choice = query_parameters.get('choice')
        win_flag = False
        if win is not None:
            win_flag = True
            choice = 'win'
        elif choice not in choice_to_num:
            random_choice = self.get_random_position()
            span.set_tag('random_choice', random_choice)
            if choice is None:
                span.log_kv(dict(event='No choice query parameter provided.'))
            span.log_kv(dict(event="Request didn't provide valid choice. "
                                   'Using {} selected at random.'.format(random_choice)))
            choice = random_choice
        else:
            span.log_kv(dict(event='Request contains valid choice {}.'.format(choice)))
        span.set_tag('win_flag', win_flag)
        return choice

    def play_roulette(self, choice, span):
        span.set_tag('choice', choice)
        with self.tracer.start_span('spin_roulette_wheel', child_of=span) as child_span:
            actual = self.spin_roulette_wheel(child_span)
        span.set_tag('actual', actual)

        if actual == choice or choice == 'win':
            raise RouletteError('Confirmation Bias!')

        return 'You lost! The ball landed on {}.'.format(actual)

    def get_random_position(self):
        return num_to_choice[randint(0, 37)]

    def spin_roulette_wheel(self, span):
        for _ in range(10000):  # simulate meaningful work
            position = self.get_random_position()
        span.set_tag('position', position)
        return position

    def wait_for_flushed_spans(self):
        # Because the Jaeger client's span reporting is asynchronous via tornado, it's not possible to reliably
        # ensure all spans have been flushed without adding a wait before using an implementation-specific
        # best attempt.
        time.sleep(.1)
        while self.tracer.reporter.queue.qsize() or self.tracer.reporter._sender.span_count:
            time.sleep(.01)
        self.tracer.close()


# Our registered Lambda handler entrypoint (example.request_handler)
def request_handler(event, context):
    player = RoulettePlayer()
    return player.handle_request(event, context)
