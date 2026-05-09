import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import extensions.BetterNeptunExtension
import extensions.Dependency
import extensions.ImplType
import extensions.betterNeptunConfig
import extensions.implementDependencies
import extensions.implementDependency
import extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidApplicationConventionPlugin: AndroidBaseConventionPlugin() {
    override val Project.commonExtension: CommonExtension
        get() = extensions.getByType(ApplicationExtension::class)

    override fun apply(target: Project) {
        target.run {
            with(pluginManager) {
                apply("com.android.application")
            }

            setupCompose()
            setupNavigation3()
            setupKoin()
            setupRoom()

            dependencies {
                implementDependency(
                    libs = libs,
                    dependency = Dependency(
                        type = ImplType.PROJECT,
                        aliases = listOf(
                            ":common",
                            ":domain",
                            ":data",
                            ":ui"
                        )
                    )
                )
                implementDependency(
                    libs = libs,
                    dependency = Dependency(
                        type = ImplType.DEPENDENCY,
                        aliases = listOf(
                            "timber",
                            "androidx-core-ktx",
                            "androidx-core-splashscreen",
                        )
                    )
                )
            }

            super.apply(this)
        }
    }
}
