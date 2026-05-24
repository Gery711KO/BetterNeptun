package hu.kocsisgeri.betterneptun.domain.model.localization

data class Language(
    val key: String,
    val localizationKey: String,
    val isDefault: Boolean,
    val isSelected: Boolean,
) {

    companion object {

        val DEFAULT = Language(
            key = "hu",
            localizationKey = "language_hu",
            isDefault = true,
            isSelected = true
        )
    }
}
