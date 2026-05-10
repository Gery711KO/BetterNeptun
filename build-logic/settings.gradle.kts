dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.version.toml"))
        }
    }
}

rootProject.name = "build-logic"
include(":convention")
