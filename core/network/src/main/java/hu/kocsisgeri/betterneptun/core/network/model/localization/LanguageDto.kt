package hu.kocsisgeri.betterneptun.core.network.model.localization

import kotlinx.serialization.Serializable

@Serializable
data class LanguageDto(
    val key: String,
    val name: String,
    val isDefault: Boolean,
)
