# SignalFx Tracing OpenTracing for .NET

This folder contains a .NET solution [SignalFx.Tracing.Samples.sln](./SignalFx.Tracing.Samples.sln)
that contains 3 projects:

- [Azure.Functions](./Azure.Functions/Azure.Functions.csproj)
- [OpenTracing.Manually.Instrumented.Library](./OpenTracing.Manually.Instrumented.Library/OpenTracing.Manually.Instrumented.Library.csproj)
- [SignalFx.OpenTracing.Manually.Instrumented.App](./SignalFx.OpenTracing.Manually.Instrumented.App/SignalFx.OpenTracing.Manually.Instrumented.App.csproj)

These projects demonstrates how to use the
[SignalFx.Tracing.OpenTracing](https://www.nuget.org/packages/SignalFx.Tracing.OpenTracing/)
and the [OpenTracing](https://www.nuget.org/packages/OpenTracing/) NuGet packages
to manually instrument .NET applications and libraries.

## Recommendations for .NET Manual Instrumention

- The manual instrumentation code should use only OpenTracing constructs. The benefit of this
approach is that if later, the application is deployed with [auto-instrumentation](../signalfx-tracing/signalfx-dotnet-tracing/README.md)
traces will keep the manually added spans and will be enriched with more spans added by
the auto-instrumentation.
- Use the [OpenTracing semantic conventions](https://github.com/opentracing/specification/blob/master/semantic_conventions.md)
especially use the `error` tag with value `true` in case of exceptions and errors.
- Libraries should use only constructs from [OpenTracing](https://opentracing.io/guides/csharp/) and don't need to
directly reference the [SignalFx.Tracing.OpenTracing NuGet Package](https://www.nuget.org/packages/SignalFx.Tracing.OpenTracing/).
- Applications need to reference `SignalFx.Tracing.Tracer.Instance` to ensure that the Tracer
runs the auto-configuration and registers itself as the `OpenTracing.Utils.GlobalTrace.Instance`. 
Example:
```csharp
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
    }```

## Useful Links

[OpenTracing Examples](https://github.com/opentracing/opentracing-csharp/tree/master/examples/OpenTracing.Examples)
