package extensions.gradle

import extensions.dependency.Dependency

val serializationPlugin = "kotlin-serialization"

val composePluginList: List<String> = listOf(
    "kotlin-compose",
)

val composeDependencies: List<Dependency>
    get() = listOf(
        Dependency(
            type = Dependency.Type.PLATFORM,
            aliases = listOf("androidx-compose-bom")
        ),
        Dependency(
            type = Dependency.Type.BUNDLE,
            aliases = listOf("compose")
        ),
    )

val navigation3dependencies: List<Dependency>
    get() = listOf(
        Dependency(
            type = Dependency.Type.BUNDLE,
            aliases = listOf("navigation3")
        ),
    )

val koinDependency: List<Dependency>
    get() = listOf(
        Dependency(
            type = Dependency.Type.BUNDLE,
            aliases = listOf("koin")
        )
    )

val featureDependencies: List<Dependency>
    get() = listOf(
        Dependency(
            type = Dependency.Type.BUNDLE,
            aliases = listOf("androidx-lifecycle")
        ),
        Dependency(
            type = Dependency.Type.DEPENDENCY,
            aliases = listOf(
                "androidx-appcompat",
                "androidx-core-ktx",
                "coil-compose"
            )
        )
    )