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

| Objective | To get the Android workshop app loaded into Android Studio and running locally |
| ---       | ---
| Duration  | 5-10 minutes                                                                   |

1. Pull down this repository into a working directory:

```
$ git clone https://github.com/signalfx/tracing-examples.git
$ cd tracing-examples/android/workshop
```

2. Open Android Studio and open the `tracing-examples/android/workshop` directory as a new
project. Android Studio should automatically detect the project and build/index it. 