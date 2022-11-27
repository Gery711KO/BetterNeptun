plugins {
    id("com.android.application")
    id(BuildPlugins.googleServicesPlugin)
    id(BuildPlugins.firebaseCrashlyticsPlugin)
    id(BuildPlugins.kotlinAndroidPlugin)
    id(BuildPlugins.kotlinAndroidExtensionsPlugin)
    id(BuildPlugins.kotlinAndroidKaptPlugin)
    id(BuildPlugins.safeArgsPlugin)
    id("org.jetbrains.kotlin.android")
}

android {
    signingConfigs {
        getByName("debug") {
            storeFile = file("/Users/gery711k/Documents/GitHub/BetterNeptun/android-debug.jks")
            storePassword = "android"
            keyAlias = "debug"
            keyPassword = "android"
        }
    }
    compileSdk = AndroidSdk.compileApi
    buildToolsVersion = AndroidSdk.buildTools

    defaultConfig {
        applicationId = "hu.kocsisgeri.betterneptun"
        minSdk = AndroidSdk.minApi
        targetSdk = AndroidSdk.targetApi
        versionCode = hu.kocsisgeri.betterneptun.Release.versionCode
        versionName = hu.kocsisgeri.betterneptun.Release.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
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
    }

    sourceSets {
        getByName("main") {
            setRoot("src/main")
            java.srcDirs("src/main/kotlin")
        }
        getByName("debug") {
            setRoot("src/debug")
            java.srcDirs("src/debug/kotlin")
        }
        getByName("release") {
            setRoot("src/release")
            java.srcDirs("src/release/kotlin")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    packagingOptions {
        resources.excludes.addAll(
            listOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/license.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/notice.txt",
                "META-INF/ASL2.0",
                "META-INF/AL2.0",
                "META-INF/LGPL2.1",
                "META-INF/*.kotlin_module"
            )
        )
    }
}

dependencies {
    implementation("androidx.room:room-common:2.4.3")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("androidx.preference:preference:1.1.1")
    kapt("androidx.room:room-compiler:2.4.3")
    implementation("androidx.room:room-ktx:2.4.3")
    implementation("org.jsoup:jsoup:1.14.3")
    implementation("androidx.work:work-runtime:2.7.1")
    implementation("com.android.support:design:29.0.0")
    implementation("androidx.fragment:fragment-ktx:1.5.2")
    implementation("androidx.compose.material3:material3:1.0.0-alpha01")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("com.github.franmontiel:PersistentCookieJar:v1.0.1")
    implementation("com.github.thellmund.Android-Week-View:core:5.2.4")
    implementation("com.github.thellmund.Android-Week-View:jsr310:5.2.4")
    implementation("io.noties.markwon:core:4.6.2")
    implementation("com.github.madrapps:pikolo:2.0.2")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    addDependency(Libraries.material)
    addDependency(Libraries.timber)

    firebase()

    koin()
    androidX()
    navigation()
    network()
    adapter()

    unitTests()
    uiTests()
}