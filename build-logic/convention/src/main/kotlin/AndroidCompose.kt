import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension,
) {
    commonExtension.apply {
//        buildFeatures {
//            compose = true
//        }

        dependencies {
            val bom = extensions.getByType(org.gradle.api.artifacts.VersionCatalogsExtension::class.java)
                .named("libs")
                .findLibrary("androidx-compose-bom")
                .get()
            add("implementation", platform(bom))
            add("androidTestImplementation", platform(bom))
        }
    }
}
