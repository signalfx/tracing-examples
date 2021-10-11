# Profiling Workshop

This workshop will guide you through running a service with the 
[Splunk Distribution of OpenTelemetry Java](https://github.com/signalfx/splunk-otel-java)
instrumentation, including the always-on profiler.

## Prerequisites

* git - You will need to be able to clone this repo
* Java 11. Find out our version of java by running `java -version`. If you do not have Java 11 you can install it with [sdkman](https://sdkman.io/install) by running `sdk install java 11.0.11.hs`.
* Docker - we will be running the [Splunk OpenTelemetry Collector Distribution](https://github.com/signalfx/splunk-otel-collector) inside Docker.
* text editor - Many folks prefer using their IDE for editing textfiles (IntelliJ IDEA) for example.
* a login for the Splunk Playground org in the [Splunk Observability Cloud](https://app.signalfx.com/)

## Sections

This workshop is broken down into the following sections. Please work through each
section completely before proceeding to the next section.

* [Part 1: Getting started](docs/01_getting_started.md) - Learn how to build and run the 
sample application and how to ingest telemetry through the collector.
* [Part 2: Enabling profiling]() - Learn how to turn on the always-on profiler, confirm
that it is working, and view the data in the Splunk Observability Cloud.
* [Part 3: Find and fix a bottleneck]() - Learn how to use the flame graph and profiling tools to 
locate and fix a bottleneck in the sample application.
