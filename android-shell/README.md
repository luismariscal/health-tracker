# Android Shell

This folder contains the Android companion app for Health Connect.

It keeps the main tracker as a web app, while giving Android native access to:
- Health Connect permissions
- weight
- steps
- sleep
- resting heart rate

Those imported records are then sent back into the same tracker UI through the native bridge.

## Fast Path

If you just want to get this open without digging around:

1. Install Android Studio.
2. Run [Open-AndroidShell.ps1](C:/Projects/Health%20Tracker/android-shell/Open-AndroidShell.ps1) or open the [android-shell](C:/Projects/Health%20Tracker/android-shell) folder manually in Android Studio.
3. Let Gradle sync finish.
4. Plug in your Android phone with USB debugging enabled, or start an emulator.
5. Press **Run** in Android Studio.
6. Open the installed app on Android.
7. Grant Health Connect access when prompted.
8. In the app, open **Health Connect** and tap **Sync from Android**.

## Browser vs Android

- The normal browser/PWA is still the main app.
- The Android shell is only needed for native Health Connect access.
- You can keep using both:
  - browser/PWA for everyday access on desktop and mobile web
  - Android shell when you want wearable import from Health Connect

## What It Does

- loads the hosted tracker inside a trusted `WebView`
- exposes a native JavaScript bridge
- requests Health Connect permissions natively
- reads Health Connect data
- returns normalized payloads to the web app

## Bridge Contract

- web page calls `HealthTrackerAndroid.requestHealthConnectSync()`
- shell requests Health Connect permission if needed
- shell imports recent records
- shell calls back into the page with `window.__healthConnectBridgeReceive(...)`

## Current Import Set

- weight
- steps
- sleep sessions
- resting heart rate

## Build Notes

Before you build:

1. Open [android-shell](C:/Projects/Health%20Tracker/android-shell) in Android Studio.
2. Confirm `WEB_APP_URL` in [app/build.gradle.kts](C:/Projects/Health%20Tracker/android-shell/app/build.gradle.kts) matches your deployed tracker URL.
3. Build and run the app on an Android device with Health Connect available.
4. If you want a quick post-build install path, use [Install-DebugApk.ps1](C:/Projects/Health%20Tracker/android-shell/Install-DebugApk.ps1) after Android Studio produces a debug APK.

Notes:

- Health Connect requires Android 9 (API 28) or higher with Google Play services.
- On Android 14 and higher, Health Connect is built into the system.
- On Android 13 and lower, users need the Health Connect app from Google Play.
- This repo does not currently include a Gradle wrapper, so Android Studio is the expected way to build unless you add wrapper files later.

## Official References

- [Get started with Health Connect](https://developer.android.com/health-and-fitness/health-connect/get-started)
- [Check Health Connect availability](https://developer.android.com/health-and-fitness/health-connect/availability)
- [PermissionController.createRequestPermissionResultContract](https://developer.android.com/reference/androidx/health/connect/client/PermissionController)
- [Build web apps in WebView](https://developer.android.com/develop/ui/views/layout/webapps/webview)
- [Access native APIs with JavaScript bridge](https://developer.android.com/develop/ui/views/layout/webapps/native-api-access-jsbridge)
