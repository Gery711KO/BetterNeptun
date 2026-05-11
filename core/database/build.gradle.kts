plugins {
    alias(libs.plugins.betterneptun.library)
}

betterNeptun {
    setupCoreDependencies(useRoom = true)
}