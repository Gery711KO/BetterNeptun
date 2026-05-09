import com.android.build.api.dsl.CommonExtension
import extensions.configureAndroidEarly
import extensions.configureAndroidLate
import org.gradle.api.Plugin
import org.gradle.api.Project

// ────────────────────────────────────────────────────────────────────────────────
// Abstract Base Convention Plugin
// ────────────────────────────────────────────────────────────────────────────────

abstract class AndroidBaseConventionPlugin(
    private val extension: Project.() -> Unit,
) : Plugin<Project> {

    abstract val Project.commonExtension: CommonExtension

    override fun apply(target: Project) {
        with(target) {
            // 1. Create extension early so user can configure it immediately
            extension()

            // 2. Set SDKs and other EARLY immutable properties
            configureAndroidEarly(commonExtension)

            // 3. Defer everything that depends on user config or can be set late
            afterEvaluate {
                // Late Android configuration (build features, dev suffix, etc.)
                configureAndroidLate(commonExtension = commonExtension)
            }
        }
    }
}