package hu.kocsisgeri.betterneptun.domain.error.model

import androidx.annotation.RawRes
import hu.kocsisgeri.betterneptun.domain.service.Localization
import kotlinx.serialization.Serializable

sealed interface ErrorContent {

    companion object

    @Serializable
    data class FullScreen(
        @RawRes val icon: Int,
        val title: Localization,
        val description: Localization,
        val primaryAction: ErrorAction,
        val secondaryAction: ErrorAction? = null,
        val inclusive: Boolean = false,
    ): ErrorContent

    data class PopUp(
        val title: Localization,
        val description: Localization,
        val button: ErrorAction.Normal,
    ) : ErrorContent

    data class Snackbar(val text: Localization) : ErrorContent
}
