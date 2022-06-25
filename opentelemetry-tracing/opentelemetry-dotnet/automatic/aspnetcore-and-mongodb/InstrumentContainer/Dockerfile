ARG SOURCE_CONTAINER
FROM ${SOURCE_CONTAINER}

# Add the OpenTelemetry .NET Automatic Instrumentation
ARG OTEL_DOTNET_AUTO_VERSION=v0.1.0-beta.1
ADD https://github.com/open-telemetry/opentelemetry-dotnet-instrumentation/releases/download/${OTEL_DOTNET_AUTO_VERSION}/opentelemetry-dotnet-instrumentation-linux-glibc.zip ./opentelemetry-dotnet-autoinstrumentation.zip

RUN apt update
RUN apt -y install zip

RUN mkdir -p /opt/opentelemetry-dotnet-autoinstrumentation
RUN unzip opentelemetry-dotnet-autoinstrumentation.zip -d /opt/opentelemetry-dotnet-autoinstrumentation
RUN rm opentelemetry-dotnet-autoinstrumentation.zip
RUN mkdir -p /var/log/opentelemetry/dotnet

ENV CORECLR_ENABLE_PROFILING=1
ENV CORECLR_PROFILER={918728DD-259F-4A6A-AC2B-B85E1B658318}
ENV CORECLR_PROFILER_PATH=/opt/opentelemetry-dotnet-autoinstrumentation/OpenTelemetry.AutoInstrumentation.Native.so
ENV DOTNET_STARTUP_HOOKS=/opt/opentelemetry-dotnet-autoinstrumentation/netcoreapp3.1/OpenTelemetry.AutoInstrumentation.StartupHook.dll
ENV OTEL_DOTNET_AUTO_INTEGRATIONS_FILE=/opt/opentelemetry-dotnet-autoinstrumentation/integrations.json
ENV OTEL_DOTNET_AUTO_HOME=/opt/opentelemetry-dotnet-autoinstrumentation
