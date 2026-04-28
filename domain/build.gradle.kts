plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "hu.kocsisgeri.betterneptun.domain"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
