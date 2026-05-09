import com.android.build.api.dsl.CommonExtension
import extensions.BETTER_NEPTUN_EXTENSION_NAME
import extensions.BetterNeptunExtension
import extensions.applyPluginFromLibs
import extensions.composeDependencies
import extensions.composePluginList
import extensions.configureAndroidEarly
import extensions.configureAndroidLate
import extensions.implementDependencies
import extensions.koinDependency
import extensions.libs
import extensions.navigation3dependencies
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

// ────────────────────────────────────────────────────────────────────────────────
// Abstract Base Convention Plugin
// ────────────────────────────────────────────────────────────────────────────────

abstract class AndroidBaseConventionPlugin : Plugin<Project> {

    abstract val Project.commonExtension: CommonExtension

    override fun apply(target: Project) {
        with(target) {
            // 1. Create extension early so user can configure it immediately
            extensions.create(BETTER_NEPTUN_EXTENSION_NAME, BetterNeptunExtension::class.java)

            // 2. Set SDKs and other EARLY immutable properties
            configureAndroidEarly(commonExtension)

            // 3. Defer everything that depends on user config or can be set late
            afterEvaluate {
                // Late Android configuration (build features, dev suffix, etc.)
                configureAndroidLate(commonExtension = commonExtension)
            }
        }
    }

    protected fun Project.setupCompose() {
        pluginManager.applyPluginFromLibs(libs to composePluginList)
        dependencies {
            implementDependencies(libs = libs, dependencyList = composeDependencies)
        }
    }

    protected fun Project.setupNavigation3() {
        dependencies {
            implementDependencies(libs = libs, dependencyList = navigation3dependencies)
        }
    }

    protected fun Project.setupRoom() {
        pluginManager.apply("betterneptun.android.room")
    }

    protected fun Project.setupKoin() {
        dependencies {
            implementDependencies(libs = libs, dependencyList = koinDependency)
        }
    }
}