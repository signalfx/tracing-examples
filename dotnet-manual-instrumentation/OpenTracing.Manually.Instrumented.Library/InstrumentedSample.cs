using System;
using System.Globalization;
using System.Threading;
using System.Threading.Tasks;

namespace OpenTracing.Manually.Instrumented.Library
{
    public class InstrumentedSample : IDisposable
    {
        private readonly SampleServer _server = new SampleServer();
        private readonly SampleClient _client = new SampleClient();

        public void Start(ushort port = 19999)
        {
            var url = $"http://localhost:{port.ToString(CultureInfo.InvariantCulture)}{SampleServer.RequestPath}/";
            _server.Start(url);
            _client.Start(url);
        }

        public void Dispose()
        {
            _client.Dispose();
            _server.Dispose();
        }

        private class SampleClient : IDisposable
        {
            private CancellationTokenSource _cts;
            private Task _requestTask;

            public void Start(string url)
            {
                _cts = new CancellationTokenSource();
                var cancellationToken = _cts.Token;

                _requestTask = Task.Run(async () =>
                    {
                        var client = new SampleClientLibrary(url);

                        while (!cancellationToken.IsCancellationRequested)
                        {
                            await client.RequestAsync($"_client message: {DateTime.Now}", cancellationToken);

                            try
                            {
                                await Task.Delay(TimeSpan.FromSeconds(1), cancellationToken).ConfigureAwait(false);
                            }
                            catch (TaskCanceledException)
                            {
                                return;
                            }
                        }
                    },
                    cancellationToken);
            }

            public void Dispose()
            {
                if (_cts != null)
                {
                    _cts.Cancel();
                    _requestTask.Wait();
                    _requestTask.Dispose();
                    _cts.Dispose();
                }
            }
        }
    }
}
