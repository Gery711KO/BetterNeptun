plugins {
    alias(libs.plugins.betterneptun.library)
}

betterNeptun {
    setupFeatureLayer("ui")
}

dependencies {
    implementation(libs.weekView.compose)
    implementation(libs.compose.colorpicker)
    implementation(libs.mpAndroidChart)
}
