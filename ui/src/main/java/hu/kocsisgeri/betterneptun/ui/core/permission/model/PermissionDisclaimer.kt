package hu.kocsisgeri.betterneptun.ui.core.permission.model

import hu.kocsisgeri.betterneptun.localization.LocalizationKey

/**
 * Represents the data required to display a rationale or disclaimer for a permission request.
 *
 * @property humanReadablePermissionName A localized key for the user-friendly name of the permission.
 * @property disclaimer A localized key for the text explaining why the permission is required.
 */
data class PermissionDisclaimer(
    val humanReadablePermissionName: LocalizationKey,
    val disclaimer: LocalizationKey,
)
