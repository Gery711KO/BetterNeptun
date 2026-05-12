plugins {
    alias(libs.plugins.betterneptun.library)
}

betterNeptun {
    setupCoreDependencies(useRoom = true) {
        implementation(libs.bundles.datastore)
    }
}