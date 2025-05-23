plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.project"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.project"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        renderscriptTargetApi = 19
        renderscriptSupportModeEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.litert.support.api)
    implementation(files("libs/zpdk-release-v3.1.aar"))
    testImplementation(libs.junit)
    implementation(libs.material.v180)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    annotationProcessor(libs.compiler)
    implementation(libs.volley)
    implementation(libs.cardview)
    implementation(libs.glide)
    implementation(libs.gson)
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    implementation(libs.gravitysnaphelper)
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    implementation(libs.blurview)
    implementation(libs.lottie)

    //zalo
    implementation(libs.okhttp)
    implementation(libs.commons.codec)
    implementation(libs.mobile.sdk)

    implementation (libs.java.jwt)
}