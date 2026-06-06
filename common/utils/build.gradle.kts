plugins {
    alias(libs.plugins.betterneptun.library)
}

betterNeptun {
    setupCommonLayer {
        api(libs.androidx.core.ktx)
        api(libs.androidx.appcompat)
        api(libs.kotlinx.serialization.json)
        api(libs.retrofit.kotlin.serialization)
    }
}
