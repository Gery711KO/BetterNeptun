plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "hu.kocsisgeri.betterneptun.domain"
    compileSdk = Config.compileSdk

    defaultConfig {
        minSdk = Config.minSdk
        testInstrumentationRunner = Config.Test.testInstrumentationRunner
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(project(":common"))
    implementation(libs.bundles.koin)
    implementation(libs.androidx.core.ktx)
}
