# Android Shell

This folder contains the long-term Android companion for Health Connect.

What it does:
- loads the hosted tracker inside a trusted `WebView`
- exposes a native bridge for Health Connect
- reads weight, steps, sleep, and resting heart rate on Android
- sends normalized records back into the existing web app

Why this shape:
- the tracker still lives in one web codebase
- Health Connect is Android-only, so the wearable import path has to be native
- the shell keeps Android permissions and Health Connect logic local without forking the product into a separate app UI

Bridge contract:
- web page calls `HealthTrackerAndroid.requestHealthConnectSync()`
- shell requests Health Connect permission if needed
- shell imports the recent records
- shell calls back into the page with `window.__healthConnectBridgeReceive(...)`

Current import set:
- weight
- steps
- sleep sessions
- resting heart rate

Before you build:
1. Open `android-shell` in Android Studio.
2. Confirm `WEB_APP_URL` in `app/build.gradle.kts` matches your deployed tracker URL.
3. Install the app on an Android device with Health Connect available.
4. Open the tracker inside the shell and use the Health Connect workflow in the app.

Notes:
- Health Connect requires Android 9 (API 28) or higher with Google Play services.
- On Android 14 and higher, Health Connect is built into the system.
- On Android 13 and lower, users need the Health Connect app from Google Play.

Official references used for this scaffold:
- [Get started with Health Connect](https://developer.android.com/health-and-fitness/health-connect/get-started)
- [Check Health Connect availability](https://developer.android.com/health-and-fitness/health-connect/availability)
- [PermissionController.createRequestPermissionResultContract](https://developer.android.com/reference/androidx/health/connect/client/PermissionController)
- [Build web apps in WebView](https://developer.android.com/develop/ui/views/layout/webapps/webview)
- [Access native APIs with JavaScript bridge](https://developer.android.com/develop/ui/views/layout/webapps/native-api-access-jsbridge)
