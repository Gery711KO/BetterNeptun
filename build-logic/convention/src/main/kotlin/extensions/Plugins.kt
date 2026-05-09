package extensions

val composePluginList: List<String> = listOf(
    "kotlin-compose",
    "kotlin-serialization"
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