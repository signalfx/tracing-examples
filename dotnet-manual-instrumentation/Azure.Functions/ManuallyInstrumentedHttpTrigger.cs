using System;
using System.IO;
using System.Threading;
using System.Threading.Tasks;
using System.Web.Http;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Azure.WebJobs;
using Microsoft.Azure.WebJobs.Extensions.Http;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json;
using OpenTracing.Manually.Instrumented.Library;
using SignalFx.Tracing.OpenTracing;

namespace Azure.Functions
{
    public static class ManuallyInstrumentedHttpTrigger
    {
        // All configurations for the SignalFxTracer are being captured from environment
        // variables defined in local.settings.json. This static is just used to trigger
        // its initialization, from this point all OpenTracing.Util.GlobalTracer.Instance
        // will be using the SignalFxTracer.
        private static readonly OpenTracing.ITracer Tracer = OpenTracingTracerFactory.WrapTracer(
            SignalFx.Tracing.Tracer.Instance
        );

        [FunctionName("ManuallyInstrumentedHttpTrigger")]
        public static async Task<IActionResult> Run(
            [HttpTrigger(AuthorizationLevel.Anonymous, "get", "post", Route = null)] HttpRequest req,
            ILogger log)
        {
            var tracer = Tracer;
            using var scope = tracer.BuildSpan("HttpTrigger").StartActive();
            
            string name = req.Query["name"];

            string requestBody = await new StreamReader(req.Body).ReadToEndAsync();
            dynamic data = JsonConvert.DeserializeObject(requestBody);
            name ??= data?.name;

            scope.Span.SetTag("span.kind", "server");
            scope.Span.SetTag("query.name", name ?? "<null>");
            scope.Span.SetTag("http.method", req.Method);

            string responseMessage = string.IsNullOrEmpty(name)
                ? "This HTTP triggered function executed successfully. Pass a name in the query string or in the request body for a personalized response."
                : $"Hello, {name}. This HTTP triggered function executed successfully.";

            ObjectResult result = null;
            try
            {
                if (name == "crash")
                {
                    // The SampleServer is not running, this will fail and will create a span if the
                    // error information.
                    var samplerServerUrl = $"http://localhost:19999{SampleServer.RequestPath}/";
                    var client = new SampleClientLibrary(samplerServerUrl);
                    responseMessage = await client.RequestAsync(responseMessage, CancellationToken.None);
                }

                result = new OkObjectResult(responseMessage);
            }
            catch (Exception e)
            {
                result = new ExceptionResult(e, includeErrorDetail: true);
                scope.Span.SetTag("error.message", e.Message);
                throw;
            }
            finally
            {
                scope.Span.SetTag("http.status_code", result?.StatusCode ?? 500);
                scope.Span.SetTag("error", true);
            }

            return result;
        }
    }
}
