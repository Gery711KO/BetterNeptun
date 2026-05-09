import com.android.build.api.dsl.ApplicationExtension
import extensions.BETTER_NEPTUN_EXTENSION_NAME
import extensions.BetterNeptunApplicationExtension
import extensions.Dependency
import extensions.ImplType
import extensions.implementDependency
import extensions.libs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidApplicationConventionPlugin: AndroidBaseConventionPlugin(
    extension = {
        extensions.add(
            BETTER_NEPTUN_EXTENSION_NAME,
            BetterNeptunApplicationExtension(project)
        )
    }
) {
    override val Project.commonExtension: ApplicationExtension
        get() = extensions.getByType(ApplicationExtension::class)

    override fun apply(target: Project) {
        target.run {
            with(pluginManager) {
                apply("com.android.application")
            }

            dependencies {
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
