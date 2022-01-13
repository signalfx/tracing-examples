# RUM Mobile Demo App

#### The app has following packages:

1. **callback**: It contains all the interfaces listeners.
2. **injection**: Dependency providing classes using Dagger2.
3. **model**: It contains all the request and responses model class and retrofit api service interface.
4. **network**: It contains all the networking library retrofit related classes.
5. **utils**: Utility classes.
6. **view**:  View classes along with their corresponding ViewModel adapter.
7. **RumDemoApp**: It is application class.

#### Classes have been designed in such a way that it could be inherited and maximize the code reuse.

### Library reference resources:
1. splunk-otel-android: https://github.com/signalfx/splunk-otel-android
2. Dagger2: https://github.com/google/dagger
3. Glide: https://github.com/bumptech/glide
4. Retrofit2: https://square.github.io/retrofit/
5. RxJava2: https://github.com/ReactiveX/RxJava
6. Androidx.navigation: https://developer.android.com/jetpack/androidx/releases/navigation
7. Parceler: https://github.com/johncarl81/parceler
