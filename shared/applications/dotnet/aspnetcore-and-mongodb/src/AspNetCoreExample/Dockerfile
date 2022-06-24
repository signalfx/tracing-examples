FROM mcr.microsoft.com/dotnet/sdk:6.0 as build

WORKDIR /build
COPY . .
RUN dotnet tool install -g Cake.Tool
ENV PATH="${PATH}:/root/.dotnet/tools"
RUN dotnet cake build.cake --runtime=linux-x64

FROM mcr.microsoft.com/dotnet/aspnet:6.0

COPY --from=build /build/publish /app
WORKDIR /app

# Add the SFx.NET Instrumentation
ARG TRACER_VERSION=0.2.4
RUN mkdir -p /var/log/signalfx
RUN mkdir -p /opt/signalfx
RUN curl -LO https://github.com/signalfx/signalfx-dotnet-tracing/releases/download/v${TRACER_VERSION}/signalfx-dotnet-tracing_${TRACER_VERSION}_amd64.deb
RUN dpkg -i ./signalfx-dotnet-tracing_${TRACER_VERSION}_amd64.deb

ENV CORECLR_ENABLE_PROFILING=1
ENV CORECLR_PROFILER={B4C89B0F-9908-4F73-9F59-0D77C5A06874}
ENV CORECLR_PROFILER_PATH=/opt/signalfx/SignalFx.Tracing.ClrProfiler.Native.so
ENV SIGNALFX_DOTNET_TRACER_HOME=/opt/signalfx

EXPOSE 5000

ENTRYPOINT ["dotnet", "AspNetCoreExample.dll"]
