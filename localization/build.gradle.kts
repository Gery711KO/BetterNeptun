plugins {
    alias(libs.plugins.betterneptun.library)
}

betterNeptun {
    setupLocalizationLayer()
}

val generateLocalizationKeys = tasks.register("generateLocalizationKeys") {
    group = "generation"
    description = "Parses the localization JSON and generates type-safe data objects and data classes."

    val jsonFile = file("src/main/assets/default_localizations.json")
    val outputDir = layout.buildDirectory.dir("generated/source/localization/main/kotlin")
    val outputFile = outputDir.get().file("hu/kocsisgeri/betterneptun/localization/LocalizationKey.kt").asFile
    
    inputs.file(jsonFile)
    outputs.dir(outputDir)

    doLast {
        if (!jsonFile.exists()) {
            throw GradleException("Localization file not found at: ${jsonFile.absolutePath}")
        }

        val jsonText = jsonFile.readText()

        val enSectionRegex = "\"en\"\\s*:\\s*\\{([^}]+)\\}".toRegex()
        val matchResult = enSectionRegex.find(jsonText)
            ?: throw GradleException("Could not find the 'en' localization section in the JSON file.")

        val enContent = matchResult.groupValues[1]

        val keyValueRegex = "\"([^\"]+)\"\\s*:\\s*\"([^\"]+)\"".toRegex()
        val items = keyValueRegex.findAll(enContent).map {
            it.groupValues[1] to it.groupValues[2]
        }.toList()

        if (items.isEmpty()) {
            throw GradleException("No localization keys found inside the 'en' block.")
        }

        val enumBuilder = StringBuilder()
        enumBuilder.appendLine("package hu.kocsisgeri.betterneptun.localization")
        enumBuilder.appendLine()
        enumBuilder.appendLine("import hu.kocsisgeri.betterneptun.domain.service.Localization")
        enumBuilder.appendLine()
        enumBuilder.appendLine("interface LocalizationKey : Localization {")
        enumBuilder.appendLine()

        items.forEach { (key, value) ->
            val argCount = value.split("%s").size - 1
            val snakeCaseName = key.uppercase()

            if (argCount == 0) {
                enumBuilder.appendLine("    data object $snakeCaseName : LocalizationKey {")
                enumBuilder.appendLine("        override val key: String = \"$key\"")
                enumBuilder.appendLine("        override val args: Array<String> = emptyArray()")
                enumBuilder.appendLine("    }")
            } else {
                val argList = (1..argCount).joinToString(", ") { "val arg$it: String" }
                val argArray = (1..argCount).joinToString(", ") { "arg$it" }

                enumBuilder.appendLine("    data class $snakeCaseName($argList) : LocalizationKey {")
                enumBuilder.appendLine("        override val key: String = \"$key\"")
                enumBuilder.appendLine("        override val args: Array<String> = arrayOf($argArray)")
                enumBuilder.appendLine("    }")
            }
        }

        enumBuilder.appendLine("}")

        outputFile.parentFile.mkdirs()
        outputFile.writeText(enumBuilder.toString())

        logger.lifecycle("Successfully generated ${items.size} keys into ${outputFile.absolutePath}")
    }
}

android {
    sourceSets {
        getByName("main") {
            kotlin.directories.add("build/generated/source/localization/main/kotlin")
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    dependsOn(generateLocalizationKeys)
    source(layout.buildDirectory.dir("generated/source/localization/main/kotlin"))
}

tasks.configureEach {
    if (name.startsWith("extract") && name.endsWith("Annotations")) {
        dependsOn(generateLocalizationKeys)
    }
}
