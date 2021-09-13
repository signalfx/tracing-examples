## Part 1: First steps

In this part of the workshop, you will get the code downloaded to your computer, and running locally, either using your
own Android device or using the Android emulator on your local computer.

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
4. Bonus step: click around the app, explore the various features (and bugs!).

---
In the next part of the workshop, you'll work on adding the Splunk RUM Android instrumentation to the project and get
telemetry flowing into the Splunk RUM product.

Next: [Part 2: Add RUM Instrumentation](part_two.md)