package hu.kocsisgeri.betterneptun.domain.service

/**
 * Represents a localizable string resource.
 */
interface Localization {

    /**
     * The unique key used to look up the translation.
     */
    val key: String

    /**
     * The fallback value to use if no translation is found for the [key].
     */
    val defaultValue: String
}
