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

import io.opentelemetry.proto.common.v1.InstrumentationScope
import io.opentelemetry.proto.common.v1.KeyValue
import io.opentelemetry.proto.logs.v1.LogRecord
import io.opentelemetry.proto.logs.v1.ResourceLogs
import io.opentelemetry.proto.metrics.v1.Metric
import io.opentelemetry.proto.metrics.v1.ResourceMetrics
import io.opentelemetry.proto.resource.v1.Resource
import io.opentelemetry.proto.trace.v1.ResourceSpans
import io.opentelemetry.proto.trace.v1.Span

data class Signals(
  val spans: Collection<ResourceSpans>,
  val metrics: Collection<ResourceMetrics>,
  val logs: Collection<ResourceLogs>
) {
  fun fromService(serviceName: String): Signals {
    return Signals(
      spans.filter { hasService(it.resource, serviceName) },
      metrics.filter { hasService(it.resource, serviceName) },
      logs.filter { hasService(it.resource, serviceName) }
    )
  }

  fun findSpans(filter: (SpanInfo) -> Boolean): List<SpanInfo> {
    val results = mutableListOf<SpanInfo>()

    spans.forEach { resourceSpans ->
      resourceSpans.scopeSpansList.forEach { scopeSpans ->
        scopeSpans.spansList.forEach { span ->
          SpanInfo(span, scopeSpans.scope, resourceSpans.resource).let {
            if (filter(it)) {
              results.add(it)
            }
          }
        }
      }
    }

    return results
  }

  fun findLogRecords(filter: (LogRecordInfo) -> Boolean): List<LogRecordInfo> {
    val results = mutableListOf<LogRecordInfo>()

    logs.forEach { resourceLogs ->
      resourceLogs.scopeLogsList.forEach { scopeLogs ->
        scopeLogs.logRecordsList.forEach { logRecord ->
          LogRecordInfo(logRecord, scopeLogs.scope, resourceLogs.resource).let {
            if (filter(it)) {
              results.add(it)
            }
          }
        }
      }
    }

    return results
  }

  fun findMetrics(filter: (MetricInfo) -> Boolean): List<MetricInfo> {
    val results = mutableListOf<MetricInfo>()

    metrics.forEach { resourceMetrics ->
      resourceMetrics.scopeMetricsList.forEach { scopeMetrics ->
        scopeMetrics.metricsList.forEach { metric ->
          MetricInfo(metric, scopeMetrics.scope, resourceMetrics.resource).let {
            if (filter(it)) {
              results.add(it)
            }
          }
        }
      }
    }

    return results
  }

  data class SpanInfo(
    val span: Span,
    val scope: InstrumentationScope,
    val resource: Resource
  ) {
    fun spanAttributes() = AttributesInfo(span.attributesList)
    fun scopeAttributes() = AttributesInfo(scope.attributesList)
    fun resourceAttributes() = AttributesInfo(resource.attributesList)
  }

  data class MetricInfo(
    val metric: Metric,
    val scope: InstrumentationScope,
    val resource: Resource
  ) {
    fun scopeAttributes() = AttributesInfo(scope.attributesList)
    fun resourceAttributes() = AttributesInfo(resource.attributesList)
  }

  data class LogRecordInfo(
    val logRecord: LogRecord,
    val scope: InstrumentationScope,
    val resource: Resource
  ) {
    fun logRecordAttributes() = AttributesInfo(logRecord.attributesList)
    fun scopeAttributes() = AttributesInfo(scope.attributesList)
    fun resourceAttributes() = AttributesInfo(resource.attributesList)
  }

  private fun hasService(resource: Resource, serviceName: String): Boolean {
    val value = resource.attributesList.find {
      it.key == "service.name"
    }?.value

    return value?.let {
      it.hasStringValue() && it.stringValue == serviceName
    } ?: false
  }

  class AttributesInfo(
    private val attributes: Collection<KeyValue>
  ) {
    fun stringValue(key: String): String? {
      attributes.find { it.key == key }?.value?.let {
        if (it.hasStringValue() || it.hasBytesValue()) {
          return it.stringValue
        }
      }
      return null
    }

    fun longValue(key: String): Long? {
      attributes.find { it.key == key }?.value?.let {
        if (it.hasIntValue()) {
          return it.intValue
        }
      }
      return null
    }
  }
}
