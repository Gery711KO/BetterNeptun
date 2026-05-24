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
            name = "LOCALIZATION_TOKEN",
            value = "\"e29780fd764641ea91df2c4b42407bb6\""
        )
    }
}