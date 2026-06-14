import extensions.gradle.SpecialLayers
import extensions.gradle.setupCompose
import extensions.gradle.setupNavigation3

plugins {
    alias(libs.plugins.betterneptun.library)
}

betterNeptun {
    setup(SpecialLayers.Navigation)
    setupCompose()
    setupNavigation3()
}
