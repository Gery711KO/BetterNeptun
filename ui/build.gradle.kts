plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "hu.kocsisgeri.betterneptun.ui"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        buildConfigField("String", "VERSION_NAME", "\"0.3.4\"")
        buildConfigField("int", "VERSION_CODE", "1")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":common"))

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.bundles.navigation3)
    
    implementation(libs.bundles.androidx.lifecycle)
    implementation(libs.bundles.koin)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime.livedata)

    implementation(libs.google.material)
    implementation(libs.timber)
    implementation(libs.weekView.core)
    implementation(libs.weekView.jsr310)
    implementation(libs.mpAndroidChart)
    implementation(libs.pikolo)
}
