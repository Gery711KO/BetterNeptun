import com.android.build.api.dsl.LibraryExtension
import extensions.config.libs
import extensions.dependency.Dependency
import extensions.dependency.implementDependency
import extensions.gradle.BETTER_NEPTUN_EXTENSION_NAME
import extensions.gradle.BetterNeptunLibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidLibraryConventionPlugin : AndroidBaseConventionPlugin() {

    override val Project.commonExtension: LibraryExtension
        get() = extensions.getByType(LibraryExtension::class)

    override fun apply(target: Project) {
        target.run {
            pluginManager.apply {
                apply("com.android.library")
            }

            extensions.add(
                BETTER_NEPTUN_EXTENSION_NAME,
                BetterNeptunLibraryExtension(target)
            )

            dependencies {
                implementDependency(
                    libs = libs,
                    dependency = Dependency(
                        type = Dependency.Type.DEPENDENCY,
                        aliases = listOf("timber")
                    )
                )
            }

            super.apply(this)
        }
    }
}
