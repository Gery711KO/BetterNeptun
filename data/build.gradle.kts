plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.android.room)
}

android {
    namespace = "hu.kocsisgeri.betterneptun.data"
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

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":common"))

    implementation(libs.bundles.androidx.room)
    ksp(libs.androidx.room.compiler)
    
    implementation(libs.bundles.koin)
    implementation(libs.bundles.networking)
    ksp(libs.moshi.codegen)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.work.runtime)
    implementation(libs.timber)
}
