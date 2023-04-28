ARG SOURCE_CONTAINER
FROM ${SOURCE_CONTAINER}

# Add the SFx.NET Instrumentation
ARG TRACER_VERSION=1.1.0
ADD https://github.com/signalfx/signalfx-dotnet-tracing/releases/download/v${TRACER_VERSION}/signalfx-dotnet-tracing_${TRACER_VERSION}_amd64.deb ./

RUN mkdir -p /var/log/signalfx
RUN mkdir -p /opt/signalfx
RUN dpkg -i ./signalfx-dotnet-tracing_${TRACER_VERSION}_amd64.deb
RUN rm ./signalfx-dotnet-tracing_${TRACER_VERSION}_amd64.deb

ENV CORECLR_ENABLE_PROFILING=1
ENV CORECLR_PROFILER={B4C89B0F-9908-4F73-9F59-0D77C5A06874}
ENV CORECLR_PROFILER_PATH=/opt/signalfx/SignalFx.Tracing.ClrProfiler.Native.so
ENV SIGNALFX_DOTNET_TRACER_HOME=/opt/signalfx
