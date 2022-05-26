# health_connect_flutter
##### Health Connect Android API Wrapper For Flutter.
Enables reading and writing health data from/to  [google Health Connect](https://developer.android.com/guide/health-and-fitness/health-connect/). 
  
## Requirements
minimum SDK Version to run is **27**
Health Connect  [APK](https://play.google.com/store/apps/details?id=com.google.android.apps.healthdata) (available on Play Store) **installed**  on user device. It will handle all requests sent by your application using the Health Connect SDK. 
## Setup

##### Android 
Add the below in your AndroidManifest.xml to handle intent that will explain your app's use of permissions.

```
<application …>
    <activity …>
        <intent-filter>
            <action android:name="androidx.health.ACTION_SHOW_PERMISSIONS_RATIONALE"/>
        </intent-filter>
        <meta-data android:name="health_permissions" android:resource="@array/health_permissions" />
    </activity> 
```
 

Declare the permissions your app will use. Create an array resource in res/values/health_permissions.xml. Note that you will need to add a line for every permission your app will use:

```
<resources>
  <array name="health_permissions">
    <item>androidx.health.permission.HeartRate.READ</item>
    <item>androidx.health.permission.HeartRate.WRITE</item>
    <item>androidx.health.permission.Steps.READ</item>
    <item>androidx.health.permission.Steps.WRITE</item>
  </array>
</resources>
```