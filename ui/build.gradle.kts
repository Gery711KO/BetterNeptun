plugins {
    alias(libs.plugins.betterneptun.library)
}

betterNeptun {
    setupFeatureLayer("ui")
}

dependencies {
    implementation(libs.bundles.androidx.lifecycle)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.compose.material3)
    implementation(libs.google.material)

    implementation(libs.weekView.compose)
    implementation(libs.compose.colorpicker)
    implementation(libs.coil.compose)

    implementation(libs.mpAndroidChart)
}
