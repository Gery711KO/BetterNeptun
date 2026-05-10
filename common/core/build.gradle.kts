plugins {
    alias(libs.plugins.betterneptun.library)
}

betterNeptun {
    setupCommonDependencies("common.core")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.okhttp)
    implementation(libs.bundles.datastore)
}
