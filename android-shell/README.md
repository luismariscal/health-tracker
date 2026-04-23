# Android Shell

This folder scaffolds the next sensible step for Health Connect: a thin Android shell around the existing web tracker.

What it does:
- loads the hosted Health Tracker web app in a `WebView`
- exposes a small `AndroidHealthConnect` JavaScript bridge
- reads Health Connect data natively on Android
- sends normalized JSON back into the existing web app

Why this shape:
- the current product is still one web app
- Health Connect is Android-only and requires the native SDK
- this keeps one source of truth instead of splitting the dashboard into separate Android and web logic

First pass sync:
- weight
- steps
- sleep
- resting heart rate

Intentional follow-up:
- workout session mapping

The web app already has the matching bridge hooks in [index.html](C:/Projects/Health%20Tracker/index.html).

Before you build:
1. Open `android-shell` in Android Studio.
2. Confirm `WEB_APP_URL` in `app/build.gradle.kts` matches your deployed tracker URL.
3. Sync Gradle and install on an Android phone with Health Connect available.

Official references used for this scaffold:
- [Get started with Health Connect](https://developer.android.com/health-and-fitness/health-connect/get-started)
- [Check Health Connect availability](https://developer.android.com/health-and-fitness/health-connect/availability)
- [Health Connect data types](https://developer.android.com/health-and-fitness/health-connect/data-types)
