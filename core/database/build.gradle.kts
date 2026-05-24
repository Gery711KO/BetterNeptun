plugins {
    alias(libs.plugins.betterneptun.library)
}

betterNeptun {
    setupCoreLayer(useRoom = true) {
        implementation(libs.bundles.datastore)
    }
}