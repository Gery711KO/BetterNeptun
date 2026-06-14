dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.version.toml"))
        }
    }
}

rootProject.name = "build-logic"
include(":convention")
