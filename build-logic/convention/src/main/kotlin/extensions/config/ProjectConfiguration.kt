package extensions.config

import org.gradle.api.JavaVersion

data class ProjectConfiguration(
    val versionName: String,
    val versionCode: Int,
    val namespace: String,
    val minSdk: Int,
    val targetSdk: Int,
    val compileSdk: Int,
    val javaVersion: JavaVersion,
)