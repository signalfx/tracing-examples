# Android RUM Instrumentation Workshop

This workshop is intended to lead you through the process of taking an existing Android app 
and adding Splunk RUM instrumentation to it.

## Prerequisites

This workshop assumes that you have done the following:

* Have `git` installed and can use it to clone open source repositories (such as this one).
* Installed [Android Studio](https://developer.android.com/studio/)
* Gone through the first 2 lessons for Android developers:
  - "Create an Android Project"  https://developer.android.com/training/basics/firstapp/creating-project
  - "Run Your App"  https://developer.android.com/training/basics/firstapp/running-app
    
This workshop assumes you are on some kind of unix-like system, such as Mac OSX with a
standard shell, like `bash` or `zsh`.

## Part 1: First steps

In this part of the workshop, you will get the code downloaded to your computer, and running locally,
either using your own Android device or using the Android simulator on your local computer.

| Objective  | To get the Android workshop app loaded into Android Studio and running locally |
| ---        | ---
| Duration   | 5-10 minutes                                                                   |
| Difficulty | Easy | 

1. Pull down this repository into a working directory:

```
$ git clone https://github.com/signalfx/tracing-examples.git
$ cd tracing-examples/android/workshop
```

2. Open Android Studio and open the `tracing-examples/android/workshop` directory as a new
project. Android Studio should automatically detect the project and build/index it. If it does not, select the `build.gradle` file in the root of the project, right-click it, and choose "Import Gradle Project".
3. Assuming everything imported correctly, you should now be able to run the workshop app on the device of your choosing. Do so now, and verify the app runs.
4. Bonus step: click around the application, explore the various features (and bugs!). 

In the next part of the workshop, we'll work on adding the Splunk RUM Android instrumentation to the project and get telemetry flowing into the Splunk RUM product.

## Part 2: Add RUM instrumentation

In part 2, we'll add the dependency on the Splunk Android RUM library, making sure that core library desugaring had been 
enabled. We'll write a simple configuration for the library, and run the application. If all goes as 
planned, telemetry data will appear in the RUM product, and we'll be able to see recording of app startup and activity/fragment tracking.
as you navigate around the app.

| Objective  | To add RUM instrumentation to the app and see data in the Splunk RUM product. |
| ---        | ---
| Duration   | 10-15 minutes | 
| Difficulty | Medium        |

1. In Android Studio, open up the `build.gradle` file for the app (not the root `build.gradle`).
2. 