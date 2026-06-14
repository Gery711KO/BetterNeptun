package hu.kocsisgeri.betterneptun.domain.error.model

import androidx.annotation.RawRes
import kotlinx.serialization.Serializable

sealed interface ErrorContent {

    companion object

    @Serializable
    data class FullScreen(
        @RawRes val icon: Int,
        val title: String,
        val description: String,
        val primaryAction: ErrorAction,
        val secondaryAction: ErrorAction? = null,
        val inclusive: Boolean = false,
    ): ErrorContent

    data class PopUp(
        val title: String,
        val description: String,
        val button: ErrorAction.Normal,
    ) : ErrorContent

    data class Snackbar(val text: String) : ErrorContent
}
