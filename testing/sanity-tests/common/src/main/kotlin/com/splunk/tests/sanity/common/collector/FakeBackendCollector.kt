/*
 * Copyright Splunk Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.splunk.tests.sanity.common.collector

import com.splunk.tests.sanity.common.Slf4jLogConsumer
import org.slf4j.LoggerFactory
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.images.builder.Transferable
import org.testcontainers.utility.DockerImageName
import java.lang.RuntimeException

class FakeBackendCollector {
  companion object {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private const val BACKEND_PORT = 8080
    private const val BACKEND_ALIAS = "backend"

    private const val COLLECTOR_ALIAS = "collector"
  }

  private var backend: GenericContainer<*>? = null
  private var collector: GenericContainer<*>? = null
  private var client: FakeBackendClient? = null

  fun start(network: Network) {
    backend = GenericContainer(DockerImageName.parse("ghcr.io/open-telemetry/opentelemetry-java-instrumentation/smoke-test-fake-backend:20220411.2147767274"))
      .withExposedPorts(BACKEND_PORT)
      .withEnv("JAVA_TOOL_OPTIONS", "-Xmx128m")
      .waitingFor(Wait.forHttp("/health").forPort(BACKEND_PORT))
      .withNetwork(network)
      .withNetworkAliases(BACKEND_ALIAS)
      .withLogConsumer(Slf4jLogConsumer(logger))
      .also { it.start() }

    collector = GenericContainer(DockerImageName.parse("quay.io/signalfx/splunk-otel-collector:0.96.1"))
      .dependsOn(backend)
      .withNetwork(network)
      .withNetworkAliases(COLLECTOR_ALIAS)
      .withLogConsumer(Slf4jLogConsumer(logger))
      .withCommand("--config=/collector-config.yaml")
      .withCopyToContainer(Transferable.of(createCollectorConfig()), "/collector-config.yaml")
      .also { it.start() }

    client = FakeBackendClient(externalBackendEndpoint())
  }

  fun internalCollectorEndpoint(): String {
    return "http://$COLLECTOR_ALIAS:4318"
  }

  fun client(): FakeBackendClient {
    return client ?: throw RuntimeException("Client not created")
  }

  fun stop() {
    backend?.stop()
    collector?.stop()
    backend = null
    collector = null
  }

  private fun externalBackendEndpoint(): String {
    val container = backend ?: throw RuntimeException("Backend not started")
    val port = container.getMappedPort(BACKEND_PORT)
    return "http://localhost:$port"
  }

  private fun createCollectorConfig(): String {
    return """
      extensions:
        health_check:
        pprof:
          endpoint: 0.0.0.0:1777
        zpages:
          endpoint: 0.0.0.0:55679
      
      receivers:
        otlp:
          protocols:
            grpc:
            http:
        signalfx:
      
      processors:
        batch:
      
      exporters:
        logging/logging_debug:
          loglevel: debug
        logging/logging_info:
          loglevel: info
        otlp:
          endpoint: backend:8080
          tls:
            insecure: true
      
      service:
        pipelines:
          traces:
            receivers: [ otlp ]
            processors: [ batch ]
            exporters: [ logging/logging_debug, otlp ]
          metrics:
            receivers: [ signalfx, otlp ]
            processors: [ batch ]
            exporters: [ logging/logging_info, otlp ]
          logs:
            receivers: [ otlp ]
            processors: [ batch ]
            exporters: [ logging/logging_info, otlp ]
      
        extensions: [ health_check, pprof, zpages ]
    """.trimIndent()
  }
}
