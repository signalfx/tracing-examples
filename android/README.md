# Android RUM Instrumentation Workshop

This workshop is intended to lead you through the process of taking an existing Android app and adding Splunk RUM
instrumentation to it.

## Prerequisites

This workshop assumes that you have done the following:

* Have `git` installed and can use it to clone open source repositories (such as this one).
* Installed [Android Studio](https://developer.android.com/studio/)
* Gone through the first 2 lessons for Android developers:
    - "Create an Android Project"  https://developer.android.com/training/basics/firstapp/creating-project
    - "Run Your App"  https://developer.android.com/training/basics/firstapp/running-app
* Have access to a Splunk RUM enabled organization in the Splunk Observability product.
* Have a
  valid [Splunk access token](https://docs.splunk.com/Observability/rum/rum.html#step-1-generate-your-rum-token-in-the-observability-cloud)
  with the `RUM` authorization scope attached.

This workshop assumes you are on some kind of unix-like system, such as Mac OSX with a standard shell, like `bash`
or `zsh`.

## Part 1: First steps

In this part of the workshop, you will get the code downloaded to your computer, and running locally, either using your
own Android device or using the Android simulator on your local computer.

| Objective  | To get the Android workshop app loaded into Android Studio and running locally |
| ---        | ---
| Duration   | 5-10 minutes                                                                   |
| Difficulty | Easy | 

1. Pull down this repository into a working directory:

```
$ git clone https://github.com/signalfx/tracing-examples.git
$ cd tracing-examples/android/workshop
```

2. Open Android Studio and open the `tracing-examples/android/workshop` directory as a new project. Android Studio
   should automatically detect the project and build/index it. If it does not, select the `build.gradle` file in the
   root of the project, right-click it, and choose "Import Gradle Project".
3. Assuming everything imported correctly, you should now be able to run the workshop app on the device of your
   choosing. Do so now, and verify the app runs.
4. Bonus step: click around the application, explore the various features (and bugs!).

In the next part of the workshop, we'll work on adding the Splunk RUM Android instrumentation to the project and get
telemetry flowing into the Splunk RUM product.

## Part 2: Add RUM instrumentation

In part 2, we'll add the dependency on the Splunk Android RUM library, making sure that core library desugaring had been
enabled. We'll write a simple configuration for the library, and run the application. If all goes as planned, telemetry
data will appear in the RUM product, and we'll be able to see recording of app startup and activity/fragment tracking.
as you navigate around the app.

| Objective  | To add RUM instrumentation to the app and see data in the Splunk RUM product. |
| ---        | ---
| Duration   | 10-15 minutes | 
| Difficulty | Medium        |

1. In Android Studio, open up the `build.gradle` file for the app module (not the root `build.gradle`). If you're using
   the default "Android" view style for the project, this will be under the "`Gradle Scripts`" folder, and labeled
   something like "`build.gradle (Module: Workshop_App.app)`"
2. Add support
   for "[core library desugaring](https://developer.android.com/studio/write/java8-support#library-desugaring)"
   Desugaring is important because both the Splunk Android RUM library and the underlying OpenTelemetry libraries use
   Java 8 language features extensively. Without desugaring support enabled, many Java 8 classes and features will not
   be available to the app at runtime and it will crash.
    1. Update the `compileOptions` section to look like this, adding the line to enable core library desugaring.
       ```
       compileOptions {
         // Flag to enable support for the new language APIs
         coreLibraryDesugaringEnabled true
         
         sourceCompatibility JavaVersion.VERSION_11
         targetCompatibility JavaVersion.VERSION_11
       }
       ```
    2. In the `dependencies` block, add this dependency on the desugar_jdk_libs library:
       ```
       coreLibraryDesugaring 'com.android.tools:desugar_jdk_libs:1.1.5'
       ```
    3. Click the "Sync Now" prompt at the top of Android Studio, build the app and run it to make sure everything is
       still working as before.
3. Add the dependency on the [splunk-otel-android](https://github.com/signalfx/splunk-otel-android) library
    1. In the `dependencies` block, add this dependency (note: if there is
       a [more recent version published](https://github.com/signalfx/splunk-otel-android/releases), please use it):
       ```
       implementation ("com.splunk:splunk-otel-android:0.6.0")
       ```   
    2. Click the "Sync Now" prompt at the top of Android Studio, build the app and run it to make sure everything is
       still working as before.
4. Next, we're going to create some configuration for the instrumentation. In general, you need to provide 3 things to
   set up instrumentation: your RUM access token, your Splunk realm, and the name of your app.
    1. Open up the `com.splunk.android.workshopapp.SampleApplication` class in Android Studio
        1. You can do by opening up the app/java/com/splunk/android/workshopapp folder in the project view.
        2. Bonus: Use the Android Studio keyboard shortcut "Navigate to Class" to open this java file.
    2. Insert the following at the beginning of the `onCreate()` method. Use your actual rumAccessToken and realm:
       ```
         Config config = Config.builder()
                 .applicationName("workshop app")
                 .rumAccessToken("<token>")
                 .realm("<realm>")
                 .deploymentEnvironment("workshop")
                 .debugEnabled(true)
                 .build();
         SplunkRum.initialize(config, this);
       ``` 
       When prompted by Android studio, add the imports for `com.splunk.rum.Config` and `com.splunk.rum.SplunkRum`
       classes to the file. Note: we're enabling debug mode here to help us debug any issues we might see during the
       workshop. In actual application, we would probably do that conditionally based on some app configuration.
    3. Feel free to change the `applicationName` and `deploymentEnvironment` options as you desire. These will be
       visible in the RUM UI, so you can customize this to make it easier for you to find your specific instance.
5. Build and run the application. Click around the app a bit to generate some data.
6. Navigate to the Splunk RUM page in the Splunk Observability product.
    1. You should be able to select your app name, and the environment that you set in the config to filter to the
       metrics that are generated by your app.
    2. Note: it may take a few minutes for the data to show up the first time that you have done this.
    3. Have fun looking at the data!
7. Bonus:
    1. Try different things in the app. Can you find the corresponding spans and metrics in the UI?
    2. Try clicking the "Crash" button in the app. What do you see in the RUM UI that might reflect that?

## Part 3: Add http client instrumentation

The Splunk Android library includes instrumentation for okhttp clients. In this part of the workshop, we'll use it when
configuring the okhttp client that is used in the sample app.

| Objective  | To add OpenTelemetry okhttp client instrumentation to the app and see client request metrics in the Splunk RUM product. |
| ---        | ---           |
| Duration   | 5-10 minutes  | 
| Difficulty | Medium        |

1. All the app's interactions with okhttp are found in the `com.splunk.android.workshopapp.FirstFragment` class. Open up
   that class in Android Studio.
2. Find the `buildOkHttpClient()` method.
3. Change the last line of the method to read:
   ```
    return builder
            .addInterceptor(SplunkRum.getInstance().createOkHttpRumInterceptor())
            .build();
   ```
   Remember to add an import for the `com.splunk.rum.SplunkRum` class when prompted by Android Studio.
4. Build and restart the app. Now try generating some http traffic by using the 3 buttons on the first screen of the app
   labeled "LOGIN", "HTTP NOT FOUND", and "HTTP ERROR".
5. Navigate to the Splunk RUM page in the Splunk Observability product.
    1. Filter for your app and environment.
    2. Try clicking on the http metrics and finding example sessions for the results. (These metrics are "Endpoint
       Errors" and "Endpoint Latency")

## Part 4: Add some manual instrumentation

The SplunkRum class in the Android RUM library contains a bunch of options for doing manual instrumentation and for
customizing the telemetry that is sent to Splunk RUM. In this part of the workshop, we'll try a couple of them, and
leave the rest for you to explore on your own.

| Objective  | To add some manually instrumented spans and events to the app. |
| ---        | ---            |
| Duration   | 10 minutes +   | 
| Difficulty | Advanced       |

#### 1. Add a "workflow" to the app, and add some custom attributes.

In Splunk's RUM implementation, a "workflow" is a custom span for which metrics will be automatically collected. In the
Android instrumentation, these workflows are modeled as OpenTelemetry Span instances.

Let's create a workflow for the user's login. This will be triggered by the button labled "LOGIN" on the first screen of
the app.

1. The code for this is in the `com.splunk.android.workshopapp.FirstFragment` class. Open up that class in Android
   Studio.
2. Find this block of code in the `onViewCreated` method:
   ```
        binding.loginButton.setOnClickListener(v -> {
            //not really a login, but it does make an http call
            makeCall("https://ssidhu.o11ystore.com/");
        });
   ```
3. Change this block of code to look like this:
   ```
        Span loginSpan = SplunkRum.getInstance().startWorkflow("Login");
        try (Scope s = loginSpan.makeCurrent()) {
            //not really a login, but it does make an http call
            makeCall("https://ssidhu.o11ystore.com/");
        } finally {
            loginSpan.end();
        }
   ```
   When prompted by Android Studio, add imports for the OpenTelemetry `Span` and `Scope` classes.
4. Add a custom "global attribute" after the `makeCall` line (but before the `finally` block):
   ```
   SplunkRum.getInstance().setGlobalAttribute(stringKey("user_id"), "123456")
   ```
   Be sure to add the static import for the OpenTelemetry `AttributeKey.stringKey()` method when prompted. A "stringKey"
   is a key for a value that must be a java `String`. There are corresponding key types for longs, doubles and booleans,
   in addition to arrays of the base types.

   The code should now look like this:

  ```
        Span loginSpan = SplunkRum.getInstance().startWorkflow("Login");
        try (Scope s = loginSpan.makeCurrent()) {
          //not really a login, but it does make an http call
          makeCall("https://ssidhu.o11ystore.com/");
          
          //add a global attribute for the user's id (pretend we got this id back from the http call)
          SplunkRum.getInstance().setGlobalAttribute(stringKey("user_id"), "123456")
        } finally {
          loginSpan.end();
        }
  ```

5. Build and restart the app. Try out the "LOGIN" button. Can you find workflow Event metrics in the UI?

#### 2. Bonus content: Try out other manual instrumentation options

Here are some other ideas to try, if you have the time:

1. Set some global attributes when building the `Config` that you created in Part 2.
2. Create your own workflows.
3. Create a custom Event or an Exception.
4. Use the OpenTelemetry APIs directly to create your own custom spans.
