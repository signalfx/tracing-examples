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

package com.splunk.tests.sanity.opentelemetry.php

import com.splunk.tests.sanity.common.ContainerHelper
import com.splunk.tests.sanity.common.collector.Signals
import com.splunk.tests.sanity.opentelemetry.php.server.ApacheApplicationServer
import com.splunk.tests.sanity.opentelemetry.php.server.ApplicationServerCustomizer
import com.splunk.tests.sanity.opentelemetry.php.server.FpmApplicationServer
import okhttp3.Request
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import java.time.Duration

class SymfonyTest : PhpBaseTest() {
  @ParameterizedTest(name = "{index} => Symfony on PHP {0} with Apache.")
  @ValueSource(strings = ["8.2", "8.3"])
  fun testSymfonyApache(phpVersion: String) {
    testWithApplication(ApacheApplicationServer(phpVersion, SymfonyCustomizer)) { endpoint, signalsFetcher ->
      testIndexRequest(endpoint, signalsFetcher)
      testGaugeMetricRequest(endpoint, signalsFetcher)
    }
  }

  @ParameterizedTest(name = "{index} => Symfony on PHP {0} with PHP-FPM+nginx.")
  @ValueSource(strings = ["8.2", "8.3"])
  fun testSymfonyFpm(phpVersion: String) {
    testWithApplication(FpmApplicationServer(phpVersion, SymfonyCustomizer)) { endpoint, signalsFetcher ->
      testIndexRequest(endpoint, signalsFetcher)
      testGaugeMetricRequest(endpoint, signalsFetcher)
    }
  }

  private fun testIndexRequest(endpoint: String, signalsFetcher: () -> Signals) {
    collector.client().clearTelemetry()

    httpClient
      .newCall(Request.Builder().url("$endpoint/en/demo/").build())
      .execute().use {
        assertEquals(it.code, 200)
        assertEquals(it.body?.string(), "Index")
      }

    val signals = waitForSignals(signalsFetcher, 10) { signals ->
      assertEquals(1, signals.findSpans {
        it.span.name == "GET demo_index"
      }.size)

      assertEquals(1, signals.findLogRecords {
        it.logRecord.body?.stringValue == "This is a sample log message."
      }.size)
    }

    val span = signals.findSpans {
      it.span.name == "GET demo_index"
    }.first()

    span.spanAttributes().let {
      assertEquals(200, it.longValue("http.response.status_code"))
      assertEquals("GET", it.stringValue("http.request.method"))
      assertEquals("/en/demo/", it.stringValue("url.path"))
      assertEquals("http", it.stringValue("url.scheme"))
    }

    span.resourceAttributes().let {
      assertEquals("opentelemetry-php-instrumentation", it.stringValue("telemetry.distro.name"))
      assertEquals("php", it.stringValue("telemetry.sdk.language"))
      assertEquals("test-php-sanity", it.stringValue("deployment.environment"))
    }

    val logRecord = signals.findLogRecords {
      it.logRecord.body?.stringValue == "This is a sample log message."
    }.first()

    assertEquals(span.span.traceId, logRecord.logRecord.traceId)
  }

  private fun testGaugeMetricRequest(endpoint: String, signalsFetcher: () -> Signals) {
    collector.client().clearTelemetry()

    httpClient
      .newCall(Request.Builder().url("$endpoint/en/demo/metric-gauge?value=33").build())
      .execute().use {
        assertEquals(it.code, 200)
        assertEquals(it.body?.string(), "Set: demo-gauge to 33")
      }

    val signals = waitForSignals(signalsFetcher, 10) { signals ->
      assertEquals(1, signals.findSpans {
        it.span.name == "GET demo_metric_gauge"
      }.size)

      assertEquals(1, signals.findMetrics {
        it.metric.name == "demo-gauge" && it.metric.gauge.dataPointsList.last().asInt == 33L
      }.size)
    }

    val span = signals.findSpans {
      it.span.name == "GET demo_metric_gauge"
    }.first()

    span.spanAttributes().let {
      assertEquals(200, it.longValue("http.response.status_code"))
      assertEquals("GET", it.stringValue("http.request.method"))
      assertEquals("/en/demo/metric-gauge", it.stringValue("url.path"))
    }
  }

  object SymfonyCustomizer : ApplicationServerCustomizer {
    override fun customizePhpContainer(container: GenericContainer<*>) {
      ContainerHelper.copyResource(container, "/application/symfony/DemoController.php", "/files/DemoController.php", false)
      ContainerHelper.copyResource(container, "/application/symfony/setup-application.sh", "/files/setup-application.sh", true)
    }

    override fun customizeHttpContainer(container: GenericContainer<*>, serverPort: Int) {
      container.waitingFor(Wait.forHttp("/en/demo/ready").forPort(serverPort).withStartupTimeout(Duration.ofMinutes(3)))
    }

    override fun documentRoot(): String {
      return "/var/www/html/example/public"
    }
  }
}
