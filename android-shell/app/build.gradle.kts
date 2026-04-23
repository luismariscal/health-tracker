plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.transformationprotocol.shell"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.transformationprotocol.shell"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
        // Review note: keep the shell pointed at the hosted web app so the
        // dashboard logic stays in one place. Update this if you move domains.
        buildConfigField(
            "String",
            "WEB_APP_URL",
            "\"https://luismariscal.github.io/health-tracker/\"",
        )
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.activity:activity-ktx:1.9.3")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.webkit:webkit:1.12.1")
    implementation("androidx.health.connect:connect-client:1.2.0-alpha04")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
}
