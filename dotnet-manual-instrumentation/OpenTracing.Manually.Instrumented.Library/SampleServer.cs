using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Text;
using System.Threading.Tasks;
using OpenTracing.Propagation;
using OpenTracing.Util;

namespace OpenTracing.Manually.Instrumented.Library
{
    public class SampleServer : IDisposable
    {
        public const string RequestPath = "/api/v1/to-upper";

        private readonly HttpListener _listener = new HttpListener();

        public void Start(string url)
        {
            _listener.Prefixes.Add(url);
            _listener.Start();

            Task.Run(() =>
            {
                var tracer = GlobalTracer.Instance;

                while (_listener.IsListening)
                {
                    try
                    {
                        var context = _listener.GetContext();

                        var startTime = DateTimeOffset.Now;
                        
                        var headerDictionary = new Dictionary<string, string>();
                        var headerKeys = context.Request.Headers.AllKeys;
                        foreach (var headerKey in headerKeys)
                        {
                            string headerValue = context.Request.Headers[headerKey];
                            headerDictionary.Add(headerKey, headerValue);
                        }

                        var requestContext = tracer.Extract(BuiltinFormats.HttpHeaders, new TextMapExtractAdapter(headerDictionary));

                        using var scope = tracer.BuildSpan($"{context.Request.HttpMethod} {context.Request.Url.AbsolutePath}")
                            .AsChildOf(requestContext)
                            .WithStartTimestamp(startTime)
                            .WithTag("span.kind", "server")
                            .StartActive();
                        foreach (var kvp in headerDictionary)
                        {
                            scope.Span.SetTag($"http.header.{kvp.Key}", kvp.Value);
                        }

                        string requestContent;
                        using (var childScope = tracer.BuildSpan("ReadStream")
                            .StartActive())
                        {
                            using var reader = new StreamReader(context.Request.InputStream, context.Request.ContentEncoding);
                            requestContent = reader.ReadToEnd();
                            childScope.Span.SetTag("request.content", requestContent);
                            childScope.Span.SetTag("request.content.length", requestContent.Length);
                        }

                        scope.Span.Finish();
                        
                        var responseContent = Encoding.UTF8.GetBytes(requestContent.ToUpperInvariant());
                        context.Response.ContentEncoding = Encoding.UTF8;
                        context.Response.ContentLength64 = responseContent.Length;
                        context.Response.OutputStream.Write(responseContent, 0, responseContent.Length);
                        context.Response.Close();
                    }
                    catch (Exception)
                    {
                        // expected when closing the _listener.
                    }
                }
            });
        }

        public void Dispose()
        {
            ((IDisposable)_listener).Dispose();
        }
    }
}