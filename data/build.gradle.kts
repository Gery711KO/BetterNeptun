plugins {
    alias(libs.plugins.betterneptun.library)
}

betterNeptun {
    setupDataLayer(
        namespaceSuffix = "data",
        useRoom = true
    )
}

dependencies {
    implementation(libs.bundles.datastore)
    implementation(libs.bundles.networking)
    implementation(libs.androidx.work.runtime)
}
