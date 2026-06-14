import com.android.build.api.dsl.CommonExtension
import extensions.config.libs
import extensions.dependency.Dependency
import extensions.dependency.implementDependencies
import extensions.gradle.configureAndroidEarly
import extensions.gradle.configureAndroidLate
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

// ────────────────────────────────────────────────────────────────────────────────
// Abstract Base Convention Plugin
// ────────────────────────────────────────────────────────────────────────────────

abstract class AndroidBaseConventionPlugin: Plugin<Project> {

    abstract val Project.commonExtension: CommonExtension

    override fun apply(target: Project) {
        with(target) {
            // 1. Set SDKs and other EARLY immutable properties
            configureAndroidEarly(commonExtension)

            target.dependencies {
                implementDependencies(
                    libs,
                    listOf(
                        Dependency(aliases = listOf("kotlinx-datetime")),
                        Dependency(
                            type = Dependency.Type.BUNDLE,
                            aliases = listOf("connectivityAndroid")
                        )
                    )
                )
            }

            // 2. Defer everything that depends on user config or can be set late
            afterEvaluate {
                // Late Android configuration (build features, dev suffix, etc.)
                configureAndroidLate(commonExtension = commonExtension)
            }
        }
    }
}
