package extensions

import org.gradle.api.provider.Property

internal const val BETTER_NEPTUN_EXTENSION_NAME = "betterNeptun"

/**
 * Base extension for BetterNeptun convention plugins.
 * Uses Gradle Property API for lazy evaluation and configuration caching.
 */
interface BetterNeptunExtension {
    val enableCompose: Property<Boolean>
    val enableRoom: Property<Boolean>
    val enableNavigation3: Property<Boolean>
    val namespaceSuffix: Property<String>
}