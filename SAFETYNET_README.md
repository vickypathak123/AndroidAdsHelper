# Safetynet (Google Play Integrity & Admob Consent Form) Usage

Check app is from trusted source or not.

## Usage

- Add below code to your `SplashActivity`, for example:

    ```kotlin
        class SplashActivity : AppCompatActivity() {
        
            override fun onCreate() {
                super.onCreate()
                    ...
        
                AppProtector.with(this@SplashActivity)
                    .appName(appName = "YOUR APP NAME")
                    .packageName(packageName = "YOUR APP PACKAGE NAME")
                    .cloudProjectNumber(cloudProjectNumber = YOUR CLOUD PROJECT NUMBER)
                    .playIntegrityRemoteConfigJson(remoteConfigJson = "PLAY INTEGRITY REMOTE CONFIG JSON")
        
                    /**
                     * Helper method to set deviceId which you can get from logs
                     * it's required field to set debug testing for google-consent
                     * Check your logcat output for the hashed device ID e.g.
                     * "Use new ConsentDebugSettings.Builder().addTestDeviceHashedId("ABCDEF012345")" to use
                     */
                    .setTestDeviceId(deviceId = "YOUR TEST DEVICE ID")
        
                    /**
                     * enable debug mode [by Default value = false]
                     * for check in test device and check all type of logs in Logcat
                     */
                    .enableDebugMode(fIsEnable = false)
        
                    /**
                     * for testing purpose
                     * need to block check integrity [by Default value = false]
                     * it pass true then not check play Integrity.
                     */
                    .needToBlockCheckIntegrity(fIsBlock = false)
        
                    /**
                     * check integrity
                     * callback when devise passed all Safetynet params
                     */
                    .checkIntegrity {
                        // TODO: perform your next action 
                    }
            }
            
        }
    ```
