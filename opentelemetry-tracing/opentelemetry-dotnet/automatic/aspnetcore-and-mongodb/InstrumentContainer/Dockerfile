ARG SOURCE_CONTAINER
FROM ${SOURCE_CONTAINER}

# Add the Splunk Distribution of OpenTelemetry .NET
ARG SPLUNK_OTEL_DOTNET_VERSION=v0.0.1-alpha.2
ADD https://github.com/signalfx/splunk-otel-dotnet/releases/download/${SPLUNK_OTEL_DOTNET_VERSION}/splunk-opentelemetry-dotnet-linux-glibc.zip ./splunk-otel-dotnet.zip

RUN apt update
RUN apt -y install zip

ARG INSTALL_DIR=/opt/splunk/otel/dotnet
RUN mkdir -p ${INSTALL_DIR}
RUN unzip splunk-otel-dotnet.zip -d ${INSTALL_DIR}
RUN rm splunk-otel-dotnet.zip
RUN mkdir -p /var/log/opentelemetry/dotnet

ENV CORECLR_ENABLE_PROFILING=1
ENV CORECLR_PROFILER={918728DD-259F-4A6A-AC2B-B85E1B658318}
ENV CORECLR_PROFILER_PATH=${INSTALL_DIR}/OpenTelemetry.AutoInstrumentation.Native.so
ENV DOTNET_ADDITIONAL_DEPS=${INSTALL_DIR}/AdditionalDeps
ENV DOTNET_SHARED_STORE=${INSTALL_DIR}/store
ENV DOTNET_STARTUP_HOOKS=${INSTALL_DIR}/netcoreapp3.1/OpenTelemetry.AutoInstrumentation.StartupHook.dll
ENV OTEL_DOTNET_AUTO_INTEGRATIONS_FILE=${INSTALL_DIR}/integrations.json
ENV OTEL_DOTNET_AUTO_HOME=${INSTALL_DIR}
