pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven("https://jetbrains.space")
    }

    versionCatalogs {
        create("libs") {
            from(files("gradle/libs.version.toml"))
        }
        create("config") {
            from(files("gradle/config.version.toml"))
        }
    }
}

rootProject.name = "BetterNeptun"
include(":app")
include(":ui")
include(":domain")
include(":data")
include(":common:utils")
include(":core:database")
include(":core:network")

