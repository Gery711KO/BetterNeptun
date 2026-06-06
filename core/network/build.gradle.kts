plugins {
    alias(libs.plugins.betterneptun.library)
}

betterNeptun {
    setupCoreLayer {
        implementation(libs.bundles.networking)
    }
}

android {
    defaultConfig {
        buildConfigField(
            type = "String",
            name = "LOCALIZATION_SNAPSHOT",
            value = "\"${config.versions.localizationSnapshot.get()}\""
        )
    }
}
