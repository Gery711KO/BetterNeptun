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
     * The arguments used for string formatting and placeholders.
     */
    val args: Array<String>
}
