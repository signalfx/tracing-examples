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

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.util.JsonFormat
import io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceRequest
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest
import io.opentelemetry.proto.logs.v1.ResourceLogs
import io.opentelemetry.proto.metrics.v1.ResourceMetrics
import io.opentelemetry.proto.trace.v1.ResourceSpans
import okhttp3.OkHttpClient
import okhttp3.Request

class FakeBackendClient(
  private val endpoint: String
) {
  companion object {
    private val OBJECT_MAPPER = ObjectMapper()
  }

  private val client = OkHttpClient()

  fun clearTelemetry() {
    client
      .newCall(Request.Builder().url("$endpoint/clear").build())
      .execute()
      .close()
  }

  fun fetchSpans(): Collection<ResourceSpans> {
    return requestPathJson("get-traces").flatMap {
      val builder = ExportTraceServiceRequest.newBuilder()
      deserialize(it, builder)
      builder.resourceSpansList
    }.toList()
  }

  fun fetchMetrics(): Collection<ResourceMetrics> {
    return requestPathJson("get-metrics").flatMap {
      val builder = ExportMetricsServiceRequest.newBuilder()
      deserialize(it, builder)
      builder.resourceMetricsList
    }.toList()
  }

  fun fetchLogs(): Collection<ResourceLogs> {
    return requestPathJson("get-logs").flatMap {
      val builder = ExportLogsServiceRequest.newBuilder()
      deserialize(it, builder)
      builder.resourceLogsList
    }.toList()
  }

  fun fetchAll(): Signals {
    return Signals(fetchSpans(), fetchMetrics(), fetchLogs())
  }

  private fun requestPathJson(path: String): Sequence<JsonNode> {
    val request = Request.Builder().url("$endpoint/$path").build()
    val content = client.newCall(request).execute().body.use {
      it?.string()
    }

    return OBJECT_MAPPER.readTree(content).iterator().asSequence()
  }

  private fun deserialize(node: JsonNode, builder: GeneratedMessageV3.Builder<*>) {
    try {
      JsonFormat.parser().merge(OBJECT_MAPPER.writeValueAsString(node), builder)
    } catch (e: Exception) {
      throw RuntimeException("Could not deserialize telemetry data", e)
    }
  }
}
