plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.navigation.safeargs)
    alias(libs.plugins.ksp)
}

android {
    namespace = "hu.kocsisgeri.betterneptun"

    compileSdk {
        version = release(libs.versions.compileSdk.get().toInt())
    }

    defaultConfig {
        applicationId = "hu.kocsisgeri.betterneptun"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "0.3.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        getByName("debug") {
            storeFile = project.file("../android-debug.jks")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }

        create("release") {
            storeFile = project.file("../android-debug.jks")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        getByName("debug") {
            versionNameSuffix = "-debug"
            isDebuggable = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    implementation(libs.bundles.androidx.room)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.bundles.navigation3)
    implementation(libs.androidx.fragment.compose)

    ksp(libs.androidx.room.compiler)
    
    implementation(libs.bundles.androidx.lifecycle)
    implementation(libs.bundles.androidx.navigation)
    implementation(libs.bundles.koin)
    implementation(libs.bundles.networking)
    implementation(libs.bundles.adapterDelegates)
    ksp(libs.moshi.codegen)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.preference)
    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.androidx.legacy.support.v4)

    implementation(libs.google.material)
    implementation(libs.timber)
    implementation(libs.jsoup)
    implementation(libs.persistentCookieJar)
    implementation(libs.weekView.core)
    implementation(libs.weekView.jsr310)
    implementation(libs.markwon.core)
    implementation(libs.pikolo)
    implementation(libs.mpAndroidChart)

    testImplementation(libs.bundles.test)
    androidTestImplementation(libs.bundles.androidTest)
}
