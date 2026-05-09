import com.android.build.api.dsl.LibraryExtension
import extensions.BETTER_NEPTUN_EXTENSION_NAME
import extensions.BetterNeptunLibraryExtension
import extensions.Dependency
import extensions.ImplType
import extensions.implementDependency
import extensions.libs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidLibraryConventionPlugin : AndroidBaseConventionPlugin(
    extension = {
        extensions.add(
            BETTER_NEPTUN_EXTENSION_NAME,
            BetterNeptunLibraryExtension(project)
        )
    }
) {

    override val Project.commonExtension: LibraryExtension
        get() = extensions.getByType(LibraryExtension::class)

    override fun apply(target: Project) {
        target.run {
            pluginManager.apply {
                apply("com.android.library")
            }

            dependencies {
                implementDependency(
                    libs = libs,
                    dependency = Dependency(
                        type = ImplType.DEPENDENCY,
                        aliases = listOf("timber")
                    )
                )
            }

            super.apply(this)
        }
    }
}
