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

package com.splunk.tests.sanity.opentelemetry.php.server

import com.splunk.tests.sanity.common.ContainerHelper
import com.splunk.tests.sanity.common.Slf4jLogConsumer
import com.splunk.tests.sanity.common.TracingConfiguration
import org.slf4j.LoggerFactory
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.images.builder.Transferable
import org.testcontainers.utility.DockerImageName

class ApacheApplicationServer(
  private val phpVersion: String,
  private val customizer: ApplicationServerCustomizer
) : PhpApplicationServer {
  companion object {
    val logger = LoggerFactory.getLogger(this::class.java)

    @JvmStatic
    val HTTP_PORT = 80
  }

  private var apacheContainer: GenericContainer<*>? = null

  override fun start(network: Network, configuration: TracingConfiguration) {
    apacheContainer = GenericContainer(DockerImageName.parse("splunk-test-images:opentelemetry-php-$phpVersion-apache"))
      .withNetwork(network)
      .withExposedPorts(HTTP_PORT)
      .also { ContainerHelper.copyResource(it, "/common/start-apache.sh", "/files/start-apache.sh", true) }
      .withCopyToContainer(Transferable.of(vhostConfig()), "/etc/apache2/sites-available/000-default.conf")
      .withCreateContainerCmdModifier {
        it.withEntrypoint("/files/start-apache.sh")
      }
      .also { ContainerHelper.addTracingConfiguration(it, configuration) }
      .withLogConsumer(Slf4jLogConsumer(logger))
      .also { customizer.customizePhpContainer(it) }
      .also { customizer.customizeHttpContainer(it, HTTP_PORT) }
      .also { it.start() }
  }

  override fun endpoint(): String {
    return apacheContainer?.let {
      "http://localhost:${it.getMappedPort(HTTP_PORT)}"
    } ?: throw RuntimeException("Application not started")
  }

  override fun close() {
    apacheContainer?.stop()
    apacheContainer = null
  }

  private fun vhostConfig(): String {
    return ContainerHelper.resourceText("/common/apache.conf")
      .replace("/var/www/html/", customizer.documentRoot())
  }
}
