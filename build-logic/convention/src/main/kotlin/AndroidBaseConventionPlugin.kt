import com.android.build.api.dsl.CommonExtension
import extensions.gradle.configureAndroidEarly
import extensions.gradle.configureAndroidLate
import org.gradle.api.Plugin
import org.gradle.api.Project

// ────────────────────────────────────────────────────────────────────────────────
// Abstract Base Convention Plugin
// ────────────────────────────────────────────────────────────────────────────────

abstract class AndroidBaseConventionPlugin: Plugin<Project> {

    abstract val Project.commonExtension: CommonExtension

    override fun apply(target: Project) {
        with(target) {
            // 1. Set SDKs and other EARLY immutable properties
            configureAndroidEarly(commonExtension)

            // 2. Defer everything that depends on user config or can be set late
            afterEvaluate {
                // Late Android configuration (build features, dev suffix, etc.)
                configureAndroidLate(commonExtension = commonExtension)
            }
        }
    }
}