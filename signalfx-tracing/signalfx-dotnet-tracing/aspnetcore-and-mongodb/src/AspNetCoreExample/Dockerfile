FROM mcr.microsoft.com/dotnet/core/sdk:3.1 as build

WORKDIR /build
COPY . .
RUN dotnet tool install -g Cake.Tool
ENV PATH="${PATH}:/root/.dotnet/tools"
RUN dotnet cake build.cake --runtime=linux-x64

FROM mcr.microsoft.com/dotnet/core/runtime:3.1

COPY --from=build /build/publish /app
WORKDIR /app

ADD https://github.com/signalfx/signalfx-dotnet-tracing/releases/latest/download/signalfx-dotnet-tracing.deb  /tmp/sdt.deb
RUN dpkg -i /tmp/sdt.deb

RUN mkdir /var/log/signalfx

EXPOSE 5000

RUN dotnet --list-runtimes

ENV CORECLR_ENABLE_PROFILING 1
ENV CORECLR_PROFILER '{B4C89B0F-9908-4F73-9F59-0D77C5A06874}'
ENV CORECLR_PROFILER_PATH /opt/signalfx-dotnet-tracing/SignalFx.Tracing.ClrProfiler.Native.so
ENV SIGNALFX_INTEGRATIONS /opt/signalfx-dotnet-tracing/integrations.json
ENV SIGNALFX_DOTNET_TRACER_HOME /opt/signalfx-dotnet-tracing

ENTRYPOINT ["dotnet", "AspNetCoreExample.dll"]
