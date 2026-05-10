plugins {
    alias(libs.plugins.betterneptun.library)
}

betterNeptun {
    setupCoreDependencies(
        namespaceSuffix = "core.database",
        useRoom = true
    )
}