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

package com.splunk.tests.sanity.common

import org.testcontainers.containers.Container
import org.testcontainers.images.builder.Transferable
import java.lang.RuntimeException

object ContainerHelper {
  fun <T : Container<*>> copyResource(container: T, resourcePath: String, destinationPath: String, executable: Boolean) {
    container.withCopyToContainer(resource(resourcePath, executable), destinationPath)
  }

  fun resourceText(resourcePath: String): String {
    return resourceBytes(resourcePath).decodeToString()
  }

  fun <T : Container<*>> addTracingConfiguration(container: T, configuration: TracingConfiguration) {
    container.withEnv("OTEL_EXPORTER_OTLP_ENDPOINT", configuration.collectorEndpoint)
      .withEnv("OTEL_SERVICE_NAME", configuration.serviceName)
      .withEnv("OTEL_RESOURCE_ATTRIBUTES", "deployment.environment=${configuration.environmentName},service.version=1.0.0")
  }

  private fun resourceBytes(path: String): ByteArray {
    val stream = this::class.java.getResourceAsStream(path) ?: throw RuntimeException("Resource $path not found")
    return stream.readBytes()
  }

  private fun resource(path: String, executable: Boolean): Transferable {
    val fileMode = Integer.parseInt(if (executable) {
      "0100755"
    } else {
      "0100644"
    }, 8)

    return Transferable.of(resourceBytes(path), fileMode)
  }
}
