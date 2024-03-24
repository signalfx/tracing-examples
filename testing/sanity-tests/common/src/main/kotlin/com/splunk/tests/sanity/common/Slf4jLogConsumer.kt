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

import org.slf4j.Logger
import org.slf4j.MDC
import org.testcontainers.containers.output.BaseConsumer
import org.testcontainers.containers.output.OutputFrame

class Slf4jLogConsumer @JvmOverloads constructor(
  private val logger: Logger,
  private var separateOutputStreams: Boolean = false
) : BaseConsumer<Slf4jLogConsumer?>() {
  private val mdc: MutableMap<String, String> = HashMap()
  private var prefix = ""

  override fun accept(outputFrame: OutputFrame) {
    val outputType = outputFrame.type
    val utf8String = outputFrame.getUtf8StringWithoutLineEnding()
    val originalMdc = MDC.getCopyOfContextMap()
    MDC.setContextMap(mdc)
    try {
      when (outputType) {
        OutputFrame.OutputType.END -> {}
        OutputFrame.OutputType.STDOUT -> if (separateOutputStreams) {
          logger.info("{}{}", if (prefix.isEmpty()) "" else "$prefix: ", utf8String)
        } else {
          logger.info("{}{}: {}", prefix, outputType, utf8String)
        }

        OutputFrame.OutputType.STDERR -> if (separateOutputStreams) {
          logger.error("{}{}", if (prefix.isEmpty()) "" else "$prefix: ", utf8String)
        } else {
          logger.info("{}{}: {}", prefix, outputType, utf8String)
        }

        else -> throw IllegalArgumentException("Unexpected outputType $outputType")
      }
    } finally {
      if (originalMdc == null) {
        MDC.clear()
      } else {
        MDC.setContextMap(originalMdc)
      }
    }
  }
}
