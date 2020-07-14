using System;
using OpenTracing.Manually.Instrumented.Library;
using SignalFx.Tracing;

namespace SignalFx.OpenTracing.Manually.Instrumented.App
{
    internal static class Program
    {
        internal static void Main(string[] args)
        {
            var signalFxTracer = Tracer.Instance;
            signalFxTracer.Settings.ServiceName = "OpenTracing.Manually.Instrumented.App";
            
            using var instrumentedSample = new InstrumentedSample();
            instrumentedSample.Start();

            Console.WriteLine("Sample is running on the background, press ENTER to stop");
            Console.ReadLine();
            Console.WriteLine("Shutting down...");
        }
    }
}
