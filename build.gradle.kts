// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("org.jetbrains.kotlin.android") version "1.7.10" apply false
    `kotlin-dsl`
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(BuildPlugins.androidBuildTools)
        classpath(BuildPlugins.kotlinGradle)
        classpath(BuildPlugins.safeArgsGradle)
        classpath(BuildPlugins.googleServices)
        classpath(BuildPlugins.firebaseCrashlytics)
        classpath(BuildPlugins.firebaseTools)
    }
}
allprojects {
    repositories {
        mavenCentral()
        google()
        maven(url = "https://jitpack.io")
    }
}
