package hu.kocsisgeri.betterneptun.domain.error

import hu.kocsisgeri.betterneptun.domain.error.model.ErrorContent
import kotlinx.coroutines.flow.SharedFlow

/**
 * Interface responsible for providing and managing the content of a full-screen error state.
 *
 * This provider allows different parts of the application to trigger a full-screen error
 * display by updating the [errorContent] flow.
 */
interface ErrorScreenContentProvider {

    /**
     * A stream of full-screen error content to be displayed to the user.
     * Subscribers will receive updates whenever a new error state is set.
     */
    val errorContent: SharedFlow<ErrorContent.FullScreen>

    /**
     * Sets and emits a new full-screen error state to the [errorContent] flow.
     *
     * @param errorContent The error details and configuration to be displayed on the full-screen error UI.
     */
    fun setErrorContent(errorContent: ErrorContent.FullScreen)
}