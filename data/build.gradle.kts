plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.android.room)
}

android {
    namespace = "hu.kocsisgeri.betterneptun.data"
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
