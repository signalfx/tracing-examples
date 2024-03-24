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

import com.splunk.tests.sanity.common.TracingConfiguration
import com.splunk.tests.sanity.common.collector.FakeBackendCollector
import com.splunk.tests.sanity.common.collector.Signals
import com.splunk.tests.sanity.opentelemetry.php.server.PhpApplicationServer
import okhttp3.OkHttpClient
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.testcontainers.containers.Network
import java.util.concurrent.ThreadLocalRandom

abstract class PhpBaseTest {
  companion object {
    @JvmStatic
    val network = Network.newNetwork()

    @JvmStatic
    val collector = FakeBackendCollector()

    @JvmStatic
    val httpClient = OkHttpClient.Builder().build()

    @BeforeAll
    @JvmStatic
    fun setup() {
      collector.start(network)
    }

    @AfterAll
    @JvmStatic
    fun shutdown() {
      collector.stop()
    }
  }

  protected fun testWithApplication(application: PhpApplicationServer, handler: (String, () -> Signals) -> Unit) {
    application.use {
      collector.client().clearTelemetry()

      val serviceName = application::class.simpleName + "-" + ThreadLocalRandom.current().nextLong()

      application.start(network, TracingConfiguration(
        serviceName,
        "test-php-sanity",
        collector.internalCollectorEndpoint()
      ))

      handler(application.endpoint()) {
        collector.client().fetchAll().fromService(serviceName)
      }
    }
  }

  protected fun waitForSignals(signalFetcher: () -> Signals, seconds: Int, check: (Signals) -> Unit): Signals {
    var attemptsRemaining = seconds

    while (true) {
      val signals = signalFetcher()

      try {
        check(signals)
        return signals
      } catch (e: AssertionError) {
        if (--attemptsRemaining > 0) {
          Thread.sleep(1000)
        } else {
          throw e
        }
      }
    }
  }
}
