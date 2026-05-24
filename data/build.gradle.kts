plugins {
    alias(libs.plugins.betterneptun.library)
}

betterNeptun {
    setupDataLayer(useRoom = true) {
        implementation(libs.bundles.datastore)
        implementation(libs.androidx.work.runtime)
    }
}
