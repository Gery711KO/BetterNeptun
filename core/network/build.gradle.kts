import extensions.config.projectConfigs

plugins {
    alias(libs.plugins.betterneptun.library)
}

betterNeptun {
    setupCoreDependencies {
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