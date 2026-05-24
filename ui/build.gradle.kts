plugins {
    alias(libs.plugins.betterneptun.library)
}

betterNeptun {
    setupFeatureLayer {
        implementation(libs.weekView.compose)
        implementation(libs.chart.compose)
        implementation(libs.compose.colorpicker)
    }
}
