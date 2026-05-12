plugins {
    alias(libs.plugins.betterneptun.library)
}

betterNeptun {
    setupCommonDependencies {
        api(libs.androidx.core.ktx)
        api(libs.androidx.appcompat)
        api(libs.kotlinx.serialization.json)
        api(libs.retrofit.kotlin.serialization)
        api(libs.okhttp)
    }
}
