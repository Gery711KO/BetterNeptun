import extensions.gradle.setupCompose

plugins {
    alias(libs.plugins.betterneptun.library)
}

betterNeptun {
    setupLocalizationLayer {
        setupCompose()
    }
}
