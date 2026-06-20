package hu.kocsisgeri.betterneptun.ui.error

import hu.kocsisgeri.betterneptun.domain.error.model.ErrorAction
import hu.kocsisgeri.betterneptun.domain.error.model.ErrorContent
import hu.kocsisgeri.betterneptun.domain.service.Localization
import hu.kocsisgeri.betterneptun.ui.designsystem.R

/**
 * Creates a standard full-screen error content with the default error lottie.
 */
fun ErrorContent.Companion.fullScreen(
    title: Localization,
    description: Localization,
    primaryAction: ErrorAction,
    secondaryAction: ErrorAction? = null,
    inclusive: Boolean = false,
) = ErrorContent.FullScreen(
    icon = R.raw.error_lottie,
    title = title,
    description = description,
    primaryAction = primaryAction,
    secondaryAction = secondaryAction,
    inclusive = inclusive,
)
