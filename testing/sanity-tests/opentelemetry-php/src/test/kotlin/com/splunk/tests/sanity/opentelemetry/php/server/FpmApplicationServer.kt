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
import com.splunk.tests.sanity.common.ContainerHelper.addTracingConfiguration
import com.splunk.tests.sanity.common.ContainerHelper.copyResource
import com.splunk.tests.sanity.common.Slf4jLogConsumer
import com.splunk.tests.sanity.common.TracingConfiguration
import org.slf4j.LoggerFactory
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.images.builder.Transferable
import org.testcontainers.utility.DockerImageName

class FpmApplicationServer(
  private val phpVersion: String,
  private val customizer: ApplicationServerCustomizer
) : PhpApplicationServer {
  companion object {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private const val HTTP_PORT = 80
    private const val FPM_ALIAS = "php-fpm"
  }

  private var fpmContainer: GenericContainer<*>? = null
  private var nginxContainer: GenericContainer<*>? = null

  override fun start(network: Network, configuration: TracingConfiguration) {
    fpmContainer = GenericContainer(DockerImageName.parse("splunk-test-images:opentelemetry-php-$phpVersion-fpm"))
      .withNetwork(network)
      .withNetworkAliases(FPM_ALIAS)
      .also { copyResource(it, "/common/start-fpm.sh", "/files/start-fpm.sh", true) }
      .withCreateContainerCmdModifier {
        it.withEntrypoint("/files/start-fpm.sh")
      }
      .also { addTracingConfiguration(it, configuration) }
      .withLogConsumer(Slf4jLogConsumer(logger))
      .also { customizer.customizePhpContainer(it) }
      .also { it.start() }

    nginxContainer = GenericContainer(DockerImageName.parse("nginx:1.25.4-alpine-slim"))
      .withNetwork(network)
      .withExposedPorts(HTTP_PORT)
      .withCopyToContainer(Transferable.of(config()), "/etc/nginx/conf.d/default.conf")
      .withLogConsumer(Slf4jLogConsumer(logger))
      .also { customizer.customizeHttpContainer(it, HTTP_PORT) }
      .also { it.start() }
  }

  override fun endpoint(): String {
    return nginxContainer?.let {
      "http://localhost:${it.getMappedPort(HTTP_PORT)}"
    } ?: throw RuntimeException("Application not started")
  }

  override fun close() {
    nginxContainer?.stop()
    nginxContainer = null
    fpmContainer?.stop()
    fpmContainer = null
  }

  private fun config(): String {
    return ContainerHelper.resourceText("/common/nginx.conf")
      .replace("/var/www/html/", customizer.documentRoot())
  }
}
