# Internet Connection State Usage

A live data of internet connection state.

## Usage

- Add Required permissions to your AndroidManifest file, for example:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />

    <application>
        ...
    </application>

</manifest>
```

- Observe an `isInternetAvailable` object in your activity or fragment files, in the onCreate method, for example:

```kotlin
class YourActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)
            ...

        isInternetAvailable.observeForever { isAvailable ->
            if (isAvailable) {
                // TODO: called when the device has a proper internet connection
            } else {
                // TODO: called when the device is not connected to any internet source 
            }
        }
    }
    
}
```