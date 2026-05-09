plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "hu.kocsisgeri.betterneptun.domain"
    compileSdk = ProjectConfig.compileSdk

    defaultConfig {
        minSdk = ProjectConfig.minSdk
        testInstrumentationRunner = ProjectConfig.Test.testInstrumentationRunner
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(projects.common)
    implementation(libs.bundles.koin)
    implementation(libs.androidx.core.ktx)
}
