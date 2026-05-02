plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "hu.kocsisgeri.betterneptun.ui"
    compileSdk = Config.compileSdk

    defaultConfig {
        minSdk = Config.minSdk
        testInstrumentationRunner = Config.Test.testInstrumentationRunner

        buildConfigField("String", "VERSION_NAME", "\"${Config.VERSION_NAME}\"")
        buildConfigField("String", "VERSION_CODE", "\"${Config.VERSION_CODE}\"")
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

    implementation(libs.weekView.compose)
    implementation(libs.compose.colorpicker)
    implementation(libs.coil.compose)

    implementation(libs.mpAndroidChart)
}
