ARG SOURCE_CONTAINER
FROM ${SOURCE_CONTAINER}

# Add the OpenTelemetry .NET Automatic Instrumentation
ARG OTEL_DOTNET_AUTO_VERSION=v0.2.0-beta.1
ADD https://github.com/open-telemetry/opentelemetry-dotnet-instrumentation/releases/download/${OTEL_DOTNET_AUTO_VERSION}/opentelemetry-dotnet-instrumentation-linux-glibc.zip ./opentelemetry-dotnet-autoinstrumentation.zip

RUN apt update
RUN apt -y install zip

ARG INSTALL_DIR=/opt/opentelemetry/dotnet/autoinstrumentation
RUN mkdir -p ${INSTALL_DIR}
RUN unzip opentelemetry-dotnet-autoinstrumentation.zip -d ${INSTALL_DIR}
RUN rm opentelemetry-dotnet-autoinstrumentation.zip
RUN mkdir -p /var/log/opentelemetry/dotnet

ENV CORECLR_ENABLE_PROFILING=1
ENV CORECLR_PROFILER={918728DD-259F-4A6A-AC2B-B85E1B658318}
ENV CORECLR_PROFILER_PATH=${INSTALL_DIR}/OpenTelemetry.AutoInstrumentation.Native.so
ENV DOTNET_ADDITIONAL_DEPS=${INSTALL_DIR}/AdditionalDeps
ENV DOTNET_SHARED_STORE=${INSTALL_DIR}/store
ENV DOTNET_STARTUP_HOOKS=${INSTALL_DIR}/netcoreapp3.1/OpenTelemetry.AutoInstrumentation.StartupHook.dll
ENV OTEL_DOTNET_AUTO_INTEGRATIONS_FILE=${INSTALL_DIR}/integrations.json
ENV OTEL_DOTNET_AUTO_HOME=${INSTALL_DIR}
