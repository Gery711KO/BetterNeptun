package hu.kocsisgeri.betterneptun.ui.screen.settings.model

data class SettingsRadioOption(
    val label: String,
    val isSelected: Boolean,
    val onClick: () -> Unit,
)
