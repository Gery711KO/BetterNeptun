package hu.kocsisgeri.betterneptun.domain.model.localization

data class LocalizationDictionary(
    val language: String,
    val localizations: Map<String, String>
)
