plugins {
    alias(libs.plugins.betterneptun.library)
}

betterNeptun {
    setupCoreDependencies {
        implementation(libs.bundles.networking)
    }
}