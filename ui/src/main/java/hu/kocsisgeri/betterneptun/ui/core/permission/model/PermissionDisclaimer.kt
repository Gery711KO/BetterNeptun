package hu.kocsisgeri.betterneptun.ui.core.permission.model

import hu.kocsisgeri.betterneptun.localization.LocalizationKey

data class PermissionDisclaimer(
    val humanReadablePermissionName: LocalizationKey,
    val disclaimer: LocalizationKey,
)
