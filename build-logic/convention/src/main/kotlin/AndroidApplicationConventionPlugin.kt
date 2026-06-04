import com.android.build.api.dsl.ApplicationExtension
import extensions.config.libs
import extensions.dependency.Dependency
import extensions.dependency.applyPluginFromLibs
import extensions.dependency.implementDependency
import extensions.gradle.BETTER_NEPTUN_EXTENSION_NAME
import extensions.gradle.BetterNeptunApplicationExtension
import extensions.gradle.koinPluginList
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidApplicationConventionPlugin: AndroidBaseConventionPlugin() {
    override val Project.commonExtension: ApplicationExtension
        get() = extensions.getByType(ApplicationExtension::class)

    override fun apply(target: Project) {
        target.run {
            with(pluginManager) {
                apply("com.android.application")
                apply("betterneptun.android.koin")
            }

            extensions.add(
                BETTER_NEPTUN_EXTENSION_NAME,
                BetterNeptunApplicationExtension(target),
            )

            dependencies {
                implementDependency(
                    libs = libs,
                    dependency = Dependency(
                        type = Dependency.Type.DEPENDENCY,
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
