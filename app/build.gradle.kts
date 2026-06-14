import com.android.build.api.dsl.ApkSigningConfig

plugins {
    alias(libs.plugins.betterneptun.application)
}

android {
    signingConfigs {
        getByName("debug") {
            setSigningConfig("DEBUG")
        }

        create("release") {
            setSigningConfig("PROD")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        getByName("debug") {
            versionNameSuffix = "-debug"
            isMinifyEnabled = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
}

private fun ApkSigningConfig.setSigningConfig(prefix: String) {
    val storeFilePath = providers.gradleProperty("${prefix}_STORE_FILE").orNull
    val storePass = providers.gradleProperty("${prefix}_STORE_PASSWORD").orNull
    val alias = providers.gradleProperty("${prefix}_KEY_ALIAS").orNull
    val keyPass = providers.gradleProperty("${prefix}_KEY_PASSWORD").orNull

    if (storeFilePath != null && storePass != null && alias != null && keyPass != null) {

        storeFile = project.file(storeFilePath)
        storePassword = storePassword
        keyAlias = alias
        keyPassword = keyPass
    }
}
