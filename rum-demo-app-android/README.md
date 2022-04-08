# RUM Mobile Demo App

A demo application that uses a sample e-commerce app flow to demonstrate the Splunk OpenTelemetry SDK 
and Splunk RUM SDK implementation.
Splunk RUM SDK provides a unified way to receive, process, and export metric, trace, and log data for Splunk Observability Cloud.

### Features

- Demonstrates the initialization of RUM SDK for the Android App.
- Demonstrates how to initialize SplunkOtelCrashReporting SDK to start listening to crashes.
- Demonstrates how to create the custom events to log on to the Splunk Observability Cloud.
- Demonstrates how to create error events to log on Splunk Observability Cloud.
- Demonstrates the way to create custom Spans and nesting of Spans.

### Supported Android OS Version

Android 5.0(Lollipop) to the latest android os version

### Library reference resources:

| **S No.** | **Name**  | **Refrence Link**  |
| :-----: | :- | :- |
| 1 | **splunk-otel-android** | https://github.com/signalfx/splunk-otel-android |
| 2 | **Dagger2** | https://github.com/google/dagger |
| 3 | **Glide** | https://github.com/bumptech/glide |
| 4 | **Retrofit2** | https://github.com/square/retrofit |
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

# Start the application in a local environment
   ### Configure "local.properties"
  
   ```properties
   rum.realm= <realm>
   rum.access.token= <a valid Splunk RUM access token for the realm>
   rum.environment= <environment name>
   ```

   ### Android studio setup
   1. Download the android studio from this link: https://developer.android.com/studio
   2. To download the required API level follow the below steps.
      - Open Tools → SDK Manager → Appearance & Behavior → System Settings → Android SDK 
        → SDK Platforms Tab select the API level and apply to download
      - SDK Tools Tab select the below items and apply to download.
        1. Android SDK build-tool 32 or the latest
        2. Android emulator
        3. Android SDK platform tools
        4. Google play services
    
   ### Project setup and run into emulator/device
   1. Check out the source code from Github.
   2. Click the open button in android studio and choose your project folder.
   3. To run the application, you must first create an emulator.
   4. Please follow the below steps to create emulators
      - Open Tools → AVD Manager → Create Virtual Device → Select Any Device → Press Next → Download Any OS → Press Finish.
   5. The emulator name now appears before the play icon in the header toolbar.
   6. When you select an emulator and click the play button, the emulator will open and the application will install and open successfully.
   7. When you want to run the application directly into your mobile phone, connect the mobile phone with your laptop, so the name of your phone will appear before the play button.
      Once you press the play button, the application will run directly onto your phone.

### Generate debug APK

1. Open up the project with android studio.
2. Select **Build>Build Bundle(s)/APK(s)>Build APK(s)** from the toolbar menu.
3. Android studio will take a few moments to generate an APK file.
4. Once the APK build is complete, you will receive a notification on the bottom right corner of android studio.
   The notification will lead you to the APK file location if you select Locate.
5. You can still locate the APK file in the following location with your project folder if you missed the notification:
   **app/build/outputs/apk/debug/app-debug.apk**
   
### Generate signed APK

1. Open up the project with android studio.
2. Select **Build>Generate Signed Bundle/APK** from the toolbar menu.
3. Create/import key store details to generate signed APKs.
4. As a final step, you must select the destination folder, select V2 (Full APK Signature), and click finish.
3. Android studio will take a few moments to generate an APK file.
5. Once the APK build is complete, you will receive a notification on the bottom right corner of android studio.
   The notification will lead you to the APK file location if you select Locate.
6. You can still locate the APK file in the following location with your project folder if you missed the notification:
   **app/release**

### Project structure:

| **S No.** | **Name**  | **Description**  |
| :-----: | :- | :- |
| 1 | **callback** | It contains all the interfaces listeners. |
| 2 | **injection** | Dependency providing classes using Dagger2. |
| 3 | **model** | It contains all the request and responses model class and retrofit API service interface. |
| 4 | **network** | It contains all the networking library retrofit related classes. |
| 5 | **service** | It contains all the services. |
| 6 | **util** | Utility classes. |
| 7 | **view** | View classes along with their corresponding ViewModel adapter. |
| 8 | **RumDemoApp** | It is application class. |

# Configuration of RUM SDK
### Initialization RUM SDK

RUM SDK integration can be achieved by configuring the following values:

* Realm.
    * You can find the realm on the Account Settings page in the Splunk Observability UI.
* RUM auth token.
    * The RUM auth token can be found or created in Splunk Observability's Organization Settings(Accessible only by the admin).
    * Important: this auth token *must* have the **RUM** authorization scope to work.
* Application Name.

Three configuration can be set in the **local.properties**

Below is an example of a minimal configuration that uses these three values:

```java

public class MyApplication extends Application {

    private void setupSplunkRUM() {

        private final String realM = "<realm>";
        private final String token = "<RUM_auth_token>";
        private final String appName = "my android app";
        
        Config config = SplunkRum.newConfigBuilder()
                .realm(realM)
                .rumAccessToken(token)
                .applicationName(appName)
                .build();
        SplunkRum.initialize(config, this);
    }
}
```
Code reference [`Here`](./app/src/main/java/com/splunk/rum/demoApp/RumDemoApp.java#L55) 

To initialize the Splunk RUM monitoring library, from your **Application** class, simply call the static initializer in your **Application.onCreate()** implementation:

```java

public class MyApplication extends Application {

   @Override
    public void onCreate() {
        super.onCreate();
        setupSplunkRUM();
    }
}
```

Code reference [`Here`](./app/src/main/java/com/splunk/rum/demoApp/RumDemoApp.java#L29) 

### Availalbe APIs:

(Note: full javadoc can be found at [javadoc.io](javadoc-url))

- The SplunkRum instrumentation uses OpenTelemetry APIs and semantic conventions for span
  generation. If you need to write your own instrumentation, the SplunkRum instance
  gives you direct access to the instance of OpenTelemetry that is being used via
  the `getOpenTelemetry()` method. For details on writing manual instrumentation, please refer to
  the [OpenTelemetry docs](https://opentelemetry.io/docs/java/manual_instrumentation/).
  
  The following is an example of SplunkRum instrumentation that uses OpenTelemetry APIs:
  
  ```java
  public class MyApplicationActivity extends AppCompatActivity {

      private void parentOne() {
          Span parentSpan = SplunkRum.getInstance().getOpenTelemetry()
                     .getTracer("SplunkRum").spanBuilder("Parent Event Name")
                     .setParent(io.opentelemetry.context.Context.current())
                     .setAttribute(stringKey("screen.name"), MyApplicationActivity.class.getSimpleName())
                     .startSpan();
    
          childOne(parentSpan);
          parentSpan.end();
       } 
      
      private void childOne(Span parentSpan) {
          Span childSpan = SplunkRum.getInstance().getOpenTelemetry()
                    .getTracer("SplunkRum").spanBuilder("Child Event Name")
                    .setParent(io.opentelemetry.context.Context.current().with(parentSpan))
                    .setAttribute(stringKey("screen.name")), MyApplicationActivity.class.getSimpleName())
                    .startSpan();
      }    
  }
  ```

  Code reference [`Here`](./app/src/main/java/com/splunk/rum/demoApp/view/urlConfig/activity/URLConfigurationActivity.java#L329) 
  
 - The SplunkRum instance exposes the RUM session ID, in case you wish to provide this to your users
   for troubleshooting purposes.
 - If you wish to record some simple Events or Workflows, the SplunkRum instance provide APIs for
  that:
    - `addRumEvent(String, Attributes)` : record a simple "zero duration" span with the provided
      name and attributes.
      
      ```java
      public class MyApplicationActivity extends AppCompatActivity {
      
         private void customEvent(){
            SplunkRum.getInstance().addRumEvent("WebViewButtonClicked", Attributes.empty());
         }
      
      }
      
      ```

      Code reference
      [`Here`](./app/src/main/java/com/splunk/rum/demoApp/view/event/fragment/LocalWebViewFragment.java#L117) 
      
    - `startWorkflow(String) : Span` : This method allows you to start a Splunk RUM "workflow" for
      which metrics will be recorded by the RUM backend. The returned OpenTelemetry **Span**
      instance *must* be ended for this workflow to be recorded.
      
      ```java
      public class MyApplicationActivity extends AppCompatActivity {
      
         // Start workflow and set status Ok
         private void startWorkFlow(){
            Span workflow = SplunkRum.getInstance().startWorkflow("ProductListLoaded");
            workflow.setStatus(StatusCode.OK, "Product list loaded successfully.");
            workflow.end();
         }
         
         // Start workflow and set status Error 
         private void startWorkFlow(){
            Span workflow = SplunkRum.getInstance().startWorkflow("ProductListLoaded");
            workflow.setStatus(StatusCode.ERROR, "The product list could not be loaded.");
            workflow.end();
         }
      
      }
      
      ```

      Code reference 
      [`Here`](./app/src/main/java/com/splunk/rum/demoApp/view/product/fragment/ProductListFragment.java#L136) 

   - To record a custom Error or Exception, SplunkRum exposes an `addRumException(Throwable)` method,
     and one that also accepts a set of **Attributes**. These exceptions will appear as errors in the RUM
     Dashboard, and error metrics will be recorded for them.

     ```java
      public final class AppUtils {
            /**
              * @param throwable Super class of all exception class
              */
             public static void handleRumException(Throwable throwable) {
                 if (SplunkRum.getInstance() != null) {
                     SplunkRum.getInstance().addRumException(throwable);
                 }
             }        
      }
      ```

      Code reference  [`Here`](./app/src/main/java/com/splunk/rum/demoApp/util/AppUtils.java#L83) 

   - If you need to update the set of "global attributes" that were initially configured, you can do
     that via one of two methods on the SplunkRum instance:  `setGlobalAttribute(AttributeKey)`
     or `updateGlobalAttributes(Consumer<AttributesBuilder> attributesUpdater)`. The former will add or
     update a single attribute, and the latter allows bulk updating of the attributes.
     
     ```java
      public class LocationService extends Service {

              private voild setLocationGlobalAttribute(double latitude, double longitude){ 
                    SplunkRum.getInstance().setGlobalAttribute(AttributeKey.doubleKey("location.lat"), latitude)
                    SplunkRum.getInstance().setGlobalAttribute(AttributeKey.doubleKey("location.long"), longitude)
              }
                      
              private voild updateLocationGlobalAttribute(double latitude, double longitude){
                    SplunkRum.getInstance().updateGlobalAttributes(attributes ->
                              attributes
                              .put("location.lat", latitude)
                              .put("location.long", longitude));
              }        
             
      }
     ```
     
   - To add OpenTelemetry instrumentation to your OkHttp3 client, SplunkRum provides an
     okhttp `Call.Factory` wrapper that can be applied to your client. See
     the `createRumOkHttpCallFactory(OkHttpClient)` for details.
     
     ```java
      public class NetworkModule {

             @Provides
             @Singleton
             Call.Factory provideOkHttpClient() {
                 OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
                 builder.hostnameVerifier((hostname, session) -> true);

                 builder.connectTimeout(60, TimeUnit.SECONDS);
                 builder.readTimeout(100, TimeUnit.SECONDS);
                 builder.writeTimeout(10, TimeUnit.MINUTES);

                 builder.addInterceptor(chain -> {
                     Request original = chain.request();
                     Request.Builder requestBuilder = original.newBuilder();
                     Request request = requestBuilder.build();
                     return chain.proceed(request);
                 });


                 return splunkRum.createRumOkHttpCallFactory(builder.build());
             }   
      }   
      ```

      Code reference [`Here`](./app/src/main/java/com/splunk/rum/demoApp/injection/module/NetworkModule.java#L50)

    - Instrument WebViews using the Browser RUM agent <br />
      Mobile RUM instrumentation and Browser RUM instrumentation can be used simultaneously by sharing the splunk.rumSessionId between both instrumentations to see RUM data combined in one stream.<br />
      The following Android snippet shows how to integrate Android RUM with Splunk Browser RUM:
         ```java
         public class LocalWebViewFragment extends BaseFragment {

                  private WebViewAssetLoader webViewAssetLoader;
                  private FragmentLocalWebViewBinding binding;
      
                  @Override
                  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                         Bundle savedInstanceState) {
            
                    if (this.getContext() != null) {
                        this.webViewAssetLoader = new WebViewAssetLoader.Builder()
                                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this.getContext()))
                                .addPathHandler("/res/", new WebViewAssetLoader.ResourcesPathHandler(this.getContext()))
                                .build();
                    }
            
                    // Inflate the layout for this fragment
                    binding = FragmentLocalWebViewBinding.inflate(inflater, container, false);
            
                    return binding.getRoot();
                  }

                   @SuppressLint("SetJavaScriptEnabled")
                   @Override
                   public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
                   super.onViewCreated(view, savedInstanceState);
                
                        if (this.webViewAssetLoader != null && getContext() != null) {
                            binding.webView.setWebViewClient(new LocalContentWebViewClient(this.webViewAssetLoader));
                            binding.webView.setWebChromeClient(new chromeClient(this.getContext()));
                            binding.webView.loadUrl("https://appassets.androidplatform.net/assets/index.html");
                
                            binding.webView.getSettings().setJavaScriptEnabled(true);
                            binding.webView.addJavascriptInterface(new WebAppInterface(getContext()), "Android");
                            SplunkRum.getInstance().integrateWithBrowserRum(binding.webView);
                        }
                   }
         }    
         ```
    
         Code reference [`Here`](./app/src/main/java/com/splunk/rum/demoApp/view/event/fragment/LocalWebViewFragment.java#L42)
      
         In your HTML file, Splunk RUM must be initialized. For example:

        ```javascript
         <script>
             SplunkRum.init({
                  beaconUrl: 'https://rum-ingest.' + Android.getRumRealm() + '.signalfx.com/v1/rum',
                  rumAuth: Android.getRumAccessToken(),
                  app: 'Android WebView'
             });          
         </script>   
        ```
         Code reference  [`Here`](./app/src/main/assets/index.html#L9)

         The session id can now be retrieved in JS code using `SplunkRumNative.getNativeSessionId()` For example:

        ```javascript
         <p>Session ID: <span id="session_id"></span></p>
            
         <script type="text/javascript">
              document.getElementById("session_id").innerHTML = SplunkRumNative.getNativeSessionId();
         </script>   
        ``` 

         Code reference  [`Here`](./app/src/main/assets/index.html#L21)