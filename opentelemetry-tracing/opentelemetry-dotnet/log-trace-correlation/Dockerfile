FROM mcr.microsoft.com/dotnet/sdk:6.0-jammy

ARG SPLUNK_OTEL_DOTNET_VERSION=v1.4.0
ADD https://github.com/signalfx/splunk-otel-dotnet/releases/download/${SPLUNK_OTEL_DOTNET_VERSION}/splunk-otel-dotnet-install.sh splunk-otel-dotnet-install.sh

RUN apt-get update && apt-get install -y unzip && \
    OTEL_DOTNET_AUTO_HOME="/splunk-otel-dotnet" sh splunk-otel-dotnet-install.sh

WORKDIR /app
COPY . .
RUN dotnet publish -c Release -o out

ENV CORECLR_ENABLE_PROFILING=1
ENV CORECLR_PROFILER={918728DD-259F-4A6A-AC2B-B85E1B658318}
ENV CORECLR_PROFILER_PATH=/splunk-otel-dotnet/linux-x64/OpenTelemetry.AutoInstrumentation.Native.so
ENV DOTNET_ADDITIONAL_DEPS=/splunk-otel-dotnet/AdditionalDeps
ENV DOTNET_SHARED_STORE=/splunk-otel-dotnet/store
ENV DOTNET_STARTUP_HOOKS=/splunk-otel-dotnet/net/OpenTelemetry.AutoInstrumentation.StartupHook.dll
ENV OTEL_DOTNET_AUTO_HOME=/splunk-otel-dotnet
ENV OTEL_DOTNET_AUTO_PLUGINS="Splunk.OpenTelemetry.AutoInstrumentation.Plugin, Splunk.OpenTelemetry.AutoInstrumentation"
