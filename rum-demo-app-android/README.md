# RUM Mobile Demo App

This is the demo application which uses a sample e-commerce app flow to demonstrate the Splunk OpenTelemetry SDK 
and Splunk RUM SDK implementation.
Splunk RUM SDK provides a unified way to receive, process, and export metric, trace, and log data for Splunk Observability Cloud.

### Features

- Demonstrates the initialisation of RUM SDK for Android App.
- Demonstrates how to initialise SplunkOtelCrashReporting SDK to start listening to crashes.
- Demonstrates how to create the custom events to log on Splunk Observability Cloud.
- Demonstrates how to create error events to log on Splunk Observability Cloud.
- Demonstrates the way to create custom Spans and nesting of Spans.

### Supported Android OS Version

Android 5.0(Lollipop) to latest android os version

### Library reference resources:

| **S No.** | **Name**  | **Refrence Link**  |
| :-----: | :- | :- |
| 1 | **splunk-otel-android** | https://github.com/signalfx/splunk-otel-android |
| 2 | **Dagger2** | https://github.com/google/dagger |
| 3 | **Glide** | https://github.com/bumptech/glide |
| 4 | **Retrofit2** | https://square.github.io/retrofit/ |
| 5 | **RxJava2** | https://github.com/ReactiveX/RxJava |
| 6 | **Androidx.navigation** | https://developer.android.com/jetpack/androidx/releases/navigation |
| 7 | **Parceler** | https://github.com/johncarl81/parceler |
| 8 | **RecyclerView** | https://developer.android.com/jetpack/androidx/releases/recyclerview |
| 9 | **CardView** | https://developer.android.com/jetpack/androidx/releases/cardview |
| 10 | **AppCompat** | https://developer.android.com/jetpack/androidx/releases/appcompat |
| 11 | **Material** | https://material.io/develop/android/docs/getting-started |
| 12 | **WebView** | https://developer.android.com/reference/androidx/webkit/package-summary |
| 13 | **AndroidX Lifecycle** | https://developer.android.com/jetpack/androidx/releases/lifecycle |
| 14 | **Location** | https://developers.google.com/android/guides/setup |
| 15 | **Play service map** | https://developers.google.com/android/guides/releases |
| 16 | **Map Utils** | https://github.com/googlemaps/android-maps-utils |



# Start the application in local environment
   ### Configure "local.properties"
  
   ```properties
   rum.realm= <realm>
   rum.access.token= <a valid Splunk RUM access token for the realm>
   rum.environment= <environment name>
   ```

   ### Android Studio Setup
   1. Download the android studio from this link: https://developer.android.com/studio
   2. To download the required API level follow the below steps.
      - Open Tools → SDK Manager → Appearance & Behavior → System Settings → Android SDK 
        → SDK Platforms Tab select the API level and apply to download
      - SDK Tools Tab select the below items and apply to download.
        1. Android SDK build-tool 32 or latest
        2. Android emulator
        3. Android sdk platform tools
        4. Google play services
    
   ### Project Setup and run into emulator/device
   1. Checkout source code from github.
   2. Click the open button in android studio and choose your project folder.
   3. To run the application, you must first create an emulator.
   4. Please follow below steps to create emulators
      - Open Tools → AVD Manager → Create Virtual Device → Select Any Device → Press Next → Download Any OS → Press Finish.
   5. The emulator name now appears before the play icon in the header toolbar.
   6. When you select an emulator and click the play button, the emulator will open and the application will install and open successfully.
   7. When you want to run the application directly into your mobile phone, connect the mobile phone with your laptop, so the name of your phone will appear before the play button.
      Once you press the play button, the application will run directly onto your phone.

### Generate Debug APK

1. Open up project with android studio.
2. Select **Build>Build Bundle(s)/APK(s)>Build APK(s)** from the toolbar menu.
3. Android studio will take a few moments to generate an APK file.
4. Once the APK build is complete, you will receive a notification on the bottom right corner of android studio.
   from that notification, select Locate and you will be led to the APK file location.
5. If you miss the notification you can still locate the APK file in the following path with your project folder:
   **app/build/outputs/apk/debug/app-debug.apk**
   
### Generate Signed APK

1. Open up project with android studio.
2. Select **Build>Generate Signed Bundle/APK** from the toolbar menu.
3. Generate signed APK you have to create/import key store details.
4. In the last step you have to select destination folder and select release,select V2 (Full APK Signature) and click on finish
3. Android studio will take a few moments to generate an APK file.
4. Once the APK build is complete, you will receive a notification on the bottom right corner of android studio.
   from that notification, select Locate and you will be led to the APK file location.
5. If you miss the notification you can still locate the APK file in the following path with your project folder:
   **app/release**

### Project structure:

| **S No.** | **Name**  | **Description**  |
| :-----: | :- | :- |
| 1 | **callback** | It contains all the interfaces listeners. |
| 2 | **injection** | Dependency providing classes using Dagger2. |
| 3 | **model** | It contains all the request and responses model class and retrofit api service interface. |
| 4 | **network** | It contains all the networking library retrofit related classes. |
| 5 | **service** | It contains all the services. |
| 6 | **util** | Utility classes. |
| 7 | **view** | View classes along with their corresponding ViewModel adapter. |
| 8 | **RumDemoApp** | It is application class. |


