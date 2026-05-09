package extensions

val serializationPlugin = "kotlin-serialization"

val composePluginList: List<String> = listOf(
    "kotlin-compose",
)

val domainDependencies: List<Dependency>
    get() = listOf(
        Dependency(
            type = ImplType.PROJECT,
            aliases = listOf(":common")
        ),
    )

val dataDependencies: List<Dependency>
    get() = listOf(
        Dependency(
            type = ImplType.PROJECT,
            aliases = listOf(
                ":common",
                ":domain",
                //":network"
            )
        ),
    )

val featureDependencies: List<Dependency>
    get() = listOf(
        Dependency(
            type = ImplType.PROJECT,
            aliases = listOf(
                ":common",
                ":domain",
            )
        ),
    )

val composeDependencies: List<Dependency>
    get() = listOf(
        Dependency(
            type = ImplType.PLATFORM,
            aliases = listOf("androidx-compose-bom")
        ),
        Dependency(
            type = ImplType.BUNDLE,
            aliases = listOf("compose")
        ),
    )

val navigation3dependencies: List<Dependency>
    get() = listOf(
        Dependency(
            type = ImplType.BUNDLE,
            aliases = listOf("navigation3")
        ),
    )

val koinDependency: List<Dependency>
    get() = listOf(
        Dependency(
            type = ImplType.BUNDLE,
            aliases = listOf("koin")
        )
    )