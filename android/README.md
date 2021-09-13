# Android RUM Instrumentation Workshop

This workshop is intended to lead you through the process of taking an existing Android app and adding Splunk RUM
instrumentation to it.

## Prerequisites

This workshop assumes that you have done the following:

* Have `git` installed and can use it to clone open source repositories (such as this one).
* Installed [Android Studio](https://developer.android.com/studio/). Be sure you have the latest released version. Check
  your install for updates if you already have it installed.
* Gone through the first 2 lessons for Android developers:
    - "Create an Android Project"
      https://developer.android.com/training/basics/firstapp/creating-project
    - "Run Your App"  https://developer.android.com/training/basics/firstapp/running-app
* Have access to a Splunk RUM enabled organization in the Splunk Observability product.
* Have a
  valid [Splunk access token](https://docs.splunk.com/Observability/rum/rum.html#step-1-generate-your-rum-token-in-the-observability-cloud)
  with the `RUM` authorization scope attached.

This workshop assumes you are on some kind of unix-like system, such as Mac OSX with a standard shell, like `bash`
or `zsh`.

By the end of this workshop, you will have instrumented an Android app and seen RUM data visible in the Splunk RUM product.

The workshop is broken up into four parts, each of which builds on the part before.

- Part 1: [First steps](docs/part_one.md)
  - Get the workshop code downloaded and the app running locally.
- Part 2: [Add RUM Instrumentation](docs/part_two.md)
  - Add the Splunk RUM instrumentation library and see RUM data in the RUM product.
- Part 3: [Add http client instrumentation](docs/part_three.md)
  - Add OpenTelemetry Instrumentation for okhttp3 and see http client spans and metrics.
- Part 4: [Add some manual instrumentation](docs/part_four.md)
  - Write some manual instrumentation and explore some advanced features of the library.
