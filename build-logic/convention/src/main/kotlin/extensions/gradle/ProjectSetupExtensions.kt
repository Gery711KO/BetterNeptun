package extensions.gradle

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import extensions.config.projectConfigs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

// ────────────────────────────────────────────────────────────────────────────────
// Extension Functions (moved here for single-file convenience)
// ────────────────────────────────────────────────────────────────────────────────
internal fun Project.configureAndroidEarly(
    commonExtension: CommonExtension,
) {
    commonExtension.apply {
        compileSdk = projectConfigs.compileSdk

        defaultConfig.minSdk = projectConfigs.minSdk
        defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        if(commonExtension is ApplicationExtension){
            commonExtension.defaultConfig {
                namespace = projectConfigs.namespace
                applicationId = projectConfigs.namespace

                targetSdk = projectConfigs.targetSdk

                versionCode = projectConfigs.versionCode
                versionName = projectConfigs.versionName


                buildConfigField(
                    type = "String",
                    name = "VERSION_NAME",
                    value = "\"${projectConfigs.versionName}\""
                )
                buildConfigField(
                    type = "String",
                    name = "VERSION_CODE",
                    value = "\"${projectConfigs.versionCode}\""
                )
            }

            commonExtension.buildFeatures {
                buildConfig = true
            }
        }

        if(commonExtension is LibraryExtension) {
            commonExtension.defaultConfig {
                buildConfigField(
                    type = "String",
                    name = "VERSION_NAME",
                    value = "\"${projectConfigs.versionName}\""
                )
                buildConfigField(
                    type = "String",
                    name = "VERSION_CODE",
                    value = "\"${projectConfigs.versionCode}\""
                )
            }

            commonExtension.buildFeatures {
                buildConfig = true
            }
        }

        compileOptions.targetCompatibility = projectConfigs.javaVersion
        compileOptions.sourceCompatibility = projectConfigs.javaVersion
    }

}


internal fun Project.configureAndroidLate(
    commonExtension: CommonExtension,
) {
    // Kotlin compiler options
    extensions.configure<KotlinAndroidProjectExtension> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
            freeCompilerArgs.addAll(
                "-opt-in=kotlin.RequiresOptIn",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlinx.coroutines.FlowPreview",
                "-Xannotation-default-target=param-property", // Silences the Hilt/UseCase warnings
                "-Xcontext-parameters" // Replaces the deprecated -Xcontext-receivers
            )
        }
    }
}