# RUM Mobile Demo App
This is the demo application which uses a sample e-commerce app flow to demonstrate the Splunk OpenTelemetry SDK and Splunk RUM SDK implementation.
Splunk RUM SDK provides a unified way to receive, process, and export metric, trace, and log data for Splunk Observability Cloud.
## Features
- Demonstrates the initialisation of the RUM SDK for iOS app.
- Demonstrates how to initialise the SplunkOtelCrashReporting SDK to start listening to crashes.
- Demonstrates how to create custom events to log on Splunk Observability Cloud.
- Demonstrates how to create error events to log on Splunk Observability Cloud.
- Demonstrates the way to create custom Spans and nesting of Spans.
## iOS Deployment Target
`iOS 11` is the minimum required OS version for iOS devices to work with the RUM SDK.
## Configuration
Before you run the app, you need to configure it with your RUM Configuration.
There are two ways you can configure the same within the RUM Demo app.
#### Configuration in Code
By this way, you can directly add your configuration values in code and then run the app in your target device to see the logs on Splunk Observability Cloud.
- Run `Cmd + Shift + o` shortcut to search for `Configuration.swift` file.
- Open `Configuration.swift` file in the Xcode project and replace the below variable values with your actual values.
```
//MARK: - Environment
let rumEnvironmentName = "ENTER_YOUR_RUM_ENVIRONMENT_NAME"
    
// MARK: - Splunk RUM BeaconURL
let beaconURL = "ENTER_YOUR_RUM_AUTH_TOKEN"
    
// MARK: - Splunk RUMAuth key
let rumAuth = "ENTER_YOUR_RUM_AUTH_TOKEN"
    
// MARK: - Splunk tracer name
let RUM_TRACER_NAME = "SplunkRum"
    
// MARK: - root URL
var rootAPIUrl : String {
    get {
            
        if let baseURL = UserDefaults.standard.value(forKey: UserDefaultKeys.appBaseURL) as? String, !baseURL.isEmpty {
            return baseURL
        }
        return "ENTER_YOUR_ROOT_API_URL"
    }
        
    set {
        UserDefaults.standard.set(newValue, forKey: UserDefaultKeys.appBaseURL)
        UserDefaults.standard.synchronize()
    }
}
```
#### Configuration in App Screen
In this way of configuration, you can run the app on your targeted iOS device and, on launch, you will be navigated to a configuration screen where you can add your values for `Rum Authentication Token` , `Environment Name` and you can also select the target `realm` from the drop down.
##### After changing the values on the app side, you must RESTART the application to get your values in effect. Remove the app from the background and relaunching the app again to set your configuration.
 \
 You can change these configurations at any time while using the app. Click on the top right button on the screen and select `Change Config URL` option.
## Package Dependencies
The demo application uses the below mentioned Swift packages.
| Plugin | Version | Link |
| ------ | ------ | ---- |
| Splunk OpenTelemetry | 0.5.3 | https://github.com/signalfx/splunk-otel-ios |
| Splunk OpenTelemetry Crash Reporting | 1.7.1 | https://github.com/signalfx/splunk-otel-ios-crashreporting |
## Cocoapods Frameworks Used
The demo application uses the below mentioned cocoapod frameworks.
| Pod | Link |
| ------ | ------ |
|IQKeyboardManager| https://cocoapods.org/pods/IQKeyboardManagerSwift |
|ObjectMapper| https://cocoapods.org/pods/ObjectMapper |
## Generating Build (Unsigned)
1. Open terminal at the folder where your Xcode project is located.
2. Run `pod install` command. You will need to install cocoapods on your system to be able to run this command.
3. Open `.xcworkspace` file from your project folder once pod installation is finished.
4. Make sure your target is selected besides the "Run" button. For Unsigned build, you can select any simulator from the dropdown.
5. Start building the project either using the shortcut `cmd + B` or you can click to `Products -> Build` in the menu bar.
6. Once the build process is successfully finished, in the project navigation, go to `Rum Mobile Demo App -> Products` folder and select the appbundle file created. The generated .app file is the Unsigned build.
## Generating Build (Signed)
To generate the signed build using Xcode, one must have an active Apple Developer Account. One needs to login to Xcode using the same developer account.
Go to `Xcode -> Preferences -> Accounts` in the menu bar and click on '+' button at the bottom. Login with your Apple Developer account here.
1. Open terminal at the folder where your Xcode project is located.
2. Run `pod install` command. You will need to install cocoapods on your system to be able to run this command.
3. Open `.xcworkspace` file from your project folder once the pod installation is finished.
4. Make sure your target is selected besides the "Run" button. For Signed build, you need to select `Any iOS Device` from the dropdown.
5. Select the project file in Project Navigator and select Target in the window, then go to `Signing & Certificates` menu. Mark the checkbox for `Automatically manage signing` and select your team from the dropdown below it.
It will create the certificates and profiles required automatically.
5. To generate the appbundle, start building the project either using the shortcut `cmd + B` or you can click on "Products -> Build` in the menu bar.
Once the build process is successfully finished, in the project navigation, go to `Rum Mobile Demo App -> Products` folder and select the appbundle file created. The generated .app file is the signed build.
#### Generate .ipa file
Perform *steps 1 to 5* from [Generate Signed Build](https://github.com/shraddha-crest/RUM-Mobile-Demo-App/blob/master/README.md#generating-build-signed) section.
1. To generate the .ipa file, select `Any iOS Device" on the target device (instead of any simulator) and click on `Products -> Archive` menu in the menu bar.
This will compile the code and create the archive. Once it is successfully compiled, it will open one window where you can see the `Distribute App` button on right side panel.
2. Select `Development` in the next step. Select Automatic Signing in the option appearing in the upcoming step and click on `Export` button at the last step where it finishes the process.
3. On the export action, it will save the archive folder to your selected location. In that folder, one will be able to find the .ipa file.
 
 
