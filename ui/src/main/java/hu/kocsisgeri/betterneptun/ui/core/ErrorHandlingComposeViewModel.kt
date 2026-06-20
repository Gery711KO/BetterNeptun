package hu.kocsisgeri.betterneptun.ui.core

import androidx.annotation.RawRes
import hu.kocsisgeri.betterneptun.domain.R
import hu.kocsisgeri.betterneptun.domain.error.ErrorRegistry
import hu.kocsisgeri.betterneptun.domain.error.model.ErrorAction
import hu.kocsisgeri.betterneptun.domain.error.model.ErrorContent
import hu.kocsisgeri.betterneptun.domain.model.ApiResult
import hu.kocsisgeri.betterneptun.domain.model.UiResult
import hu.kocsisgeri.betterneptun.domain.model.mapToUiResult
import hu.kocsisgeri.betterneptun.domain.service.Localization
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


/**
 * An extension of [ComposeViewModel] that integrates with [ErrorRegistry] to provide
 * standardized error handling mechanisms for UI flows.
 *
 * This class provides utility functions to automatically intercept errors within [Flow]
 * and [ApiResult] streams and register them as UI-visible error states such as
 * Snackbars or Full-Screen error views, while also mapping domain results into [UiResult].
 *
 * @property errorRegistry The registry used to manage and broadcast error events to the UI.
 */
abstract class ErrorHandlingComposeViewModel(
    private val errorRegistry: ErrorRegistry
) : ComposeViewModel() {

    /**
     * Registers the flow to the general error handling mechanism.
     *
     * Each emission from the flow is passed to the [contentResolver]. If the resolver returns
     * an [ErrorContent] object, the error is dispatched through the [errorRegistry] to be
     * displayed on the UI.
     *
     * @param T The type of data emitted by the flow.
     * @param contentResolver A suspendable function that determines if an emission represents
     * an error and returns the corresponding [ErrorContent], or `null` if no error occurred.
     * @return The original flow for further processing.
     */
    protected fun <T> Flow<T>.registerToGeneralErrors(
        contentResolver: suspend (T) -> ErrorContent?
    ): Flow<T> = with(errorRegistry) {
        registerToGeneralErrors { contentResolver(it) }
    }

    /**
     * Registers errors from an [ApiResult] flow to the [ErrorRegistry] and transforms the stream into a [UiResult] flow.
     *
     * If the flow emits an [ApiResult.Error], the provided [contentResolver] is used to determine how the error
     * should be presented (e.g., Snackbar or FullScreen). The resulting flow maps both [ApiResult.Loading]
     * and [ApiResult.Error] to [UiResult.Loading], while [ApiResult.Success] is mapped to [UiResult.Success].
     *
     */
    protected fun <T : Any> Flow<ApiResult<T>>.registerApiResultToGeneralErrors(
        contentResolver: suspend (error: String) -> ErrorContent?
    ) = with(errorRegistry) {
        registerToGeneralErrors { result ->
            when (result) {
                is ApiResult.Error -> contentResolver(result.error)
                else -> null
            }
        }.map { result ->
            result.mapToUiResult()
        }
    }

    /**
     * Registers errors from an [ApiResult] flow to the [ErrorRegistry] to be displayed as Snackbars
     * and transforms the stream into a [UiResult] flow.
     *
     * When the flow emits an [ApiResult.Error], the error message is automatically wrapped in an
     * [ErrorContent.Snackbar] and dispatched to the registry. The resulting flow maps
     * [ApiResult.Success] to [UiResult.Success], while [ApiResult.Loading] and [ApiResult.Error]
     * are both mapped to [UiResult.Loading].
     *
     * @param T The type of data encapsulated by the [ApiResult].
     */
    protected fun <T : Any> Flow<ApiResult<T>>.registerApiResultToGeneralSnackBarError(
        error: Localization
    ) = with(errorRegistry) {
        registerToGeneralErrors { result ->
            when (result) {
                is ApiResult.Error -> ErrorContent.Snackbar(error)
                else -> null
            }
        }.map { result ->
            result.mapToUiResult()
        }
    }

    /**
     * Registers errors from an [ApiResult] flow to the [ErrorRegistry] as a full-screen error state
     * and transforms the stream into a [UiResult] flow.
     *
     * If the flow emits an [ApiResult.Error], it is captured and registered as an [ErrorContent.FullScreen]
     * using the provided parameters. The resulting flow maps [ApiResult.Loading] and [ApiResult.Error]
     * to [UiResult.Loading], while [ApiResult.Success] is mapped to [UiResult.Success].
     *
     * @param T The type of data contained in the [ApiResult].
     * @param description The detailed text to display on the full-screen error view.
     * @param primaryAction The main action (e.g., "Retry") to be displayed on the error screen.
     * @param icon The Lottie animation or raw resource ID to display as the error graphic. Defaults to a standard error Lottie.
     * @param secondaryAction An optional secondary action (e.g., "Go back") for the error screen.
     * @param inclusive Whether the error screen should cover the entire navigation stack or specific UI components.
     * @return A flow of [UiResult], where errors and loading states are represented as [UiResult.Loading].
     */
    protected fun <T : Any> Flow<ApiResult<T>>.registerApiResultToGeneralFullScreenError(
        title: Localization,
        description: Localization,
        primaryAction: ErrorAction,
        @RawRes icon: Int = R.raw.error_lottie,
        secondaryAction: ErrorAction? = null,
        inclusive: Boolean = false,
    ) = with(errorRegistry) {
        registerToGeneralErrors { result ->
            when (result) {
                is ApiResult.Error -> ErrorContent.FullScreen(
                    icon = icon,
                    title = title,
                    description = description,
                    primaryAction = primaryAction,
                    secondaryAction = secondaryAction,
                    inclusive = inclusive
                )

                else -> null
            }
        }.map { result ->
            result.mapToUiResult()
        }
    }
}
