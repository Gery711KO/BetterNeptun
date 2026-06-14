import extensions.config.libs
import extensions.dependency.Dependency
import extensions.dependency.implementDependency
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.koin.compiler.plugin.KoinGradleExtension

class AndroidKoinConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("io.insert-koin.compiler.plugin")

            extensions.configure<KoinGradleExtension> {
                compileSafety.set(true)
                strictSafety.set(true)
                userLogs.set(true)
            }

            dependencies {
                implementDependency(
                    libs = libs,
                    dependency = Dependency(
                        type = Dependency.Type.BUNDLE,
                        aliases = listOf("koin")
                    )
                )
            }
        }
    }
}
