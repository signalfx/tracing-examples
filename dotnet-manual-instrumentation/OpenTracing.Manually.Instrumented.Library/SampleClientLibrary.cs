using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using OpenTracing.Util;
using OpenTracing.Propagation;

namespace OpenTracing.Manually.Instrumented.Library
{
    public class SampleClientLibrary
    {
        private readonly Uri _uri;
        private readonly HttpClient _client;
        
        public SampleClientLibrary(string serverUrl)
        {
            _uri = new Uri(serverUrl);
            _client = new HttpClient();
        }
        
        public async Task<string> RequestAsync(string content, CancellationToken cancellationToken)
        {
            string responseContent;

            var request = new HttpRequestMessage
            {
                RequestUri = _uri,
                Method = HttpMethod.Post,
                Content = new StringContent(content, Encoding.UTF8),
            };

            var tracer = GlobalTracer.Instance;
            using (var scope = tracer.BuildSpan("Client POST " + _uri.AbsolutePath)
                .WithTag("span.kind", "client")
                .StartActive())
            {
                var contextPropagationHeaders = new Dictionary<string, string>();
                var contextCarrier = new TextMapInjectAdapter(contextPropagationHeaders);
                tracer.Inject(scope.Span.Context,BuiltinFormats.TextMap, contextCarrier);
                foreach (var kvp in contextPropagationHeaders)
                {
                    request.Headers.Add(kvp.Key, kvp.Value);
                }

                using var response = await _client.SendAsync(request, cancellationToken);

                scope.Span.SetTag("http.status_code", (int)response.StatusCode);

                responseContent = await response.Content.ReadAsStringAsync();
                scope.Span.SetTag("response.content", responseContent);
                scope.Span.SetTag("response.length", responseContent.Length);

                foreach (var header in response.Headers)
                {
                    if (header.Value is IEnumerable<object> enumerable)
                    {
                        scope.Span.SetTag($"http.header.{header.Key}", string.Join(",", enumerable));
                    }
                    else
                    {
                        scope.Span.SetTag($"http.header.{header.Key}", header.Value.ToString());
                    }
                }
            }

            return responseContent;
        }
    }
}