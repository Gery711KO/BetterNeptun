package hu.kocsisgeri.betterneptun.ui.screen.settings.model

import hu.kocsisgeri.betterneptun.localization.LocalizationKey

data class SettingsRadioOption(
    val label: String,
    val isSelected: Boolean,
    val onClick: () -> Unit,
)
