package hu.kocsisgeri.betterneptun.ui.core

import androidx.annotation.RawRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import hu.kocsisgeri.betterneptun.domain.R
import hu.kocsisgeri.betterneptun.domain.error.ErrorRegistry
import hu.kocsisgeri.betterneptun.domain.error.model.ErrorAction
import hu.kocsisgeri.betterneptun.domain.error.model.ErrorContent
import hu.kocsisgeri.betterneptun.domain.model.ApiResult
import hu.kocsisgeri.betterneptun.domain.model.UiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlin.reflect.KClass

/**
 * A base [ViewModel] class providing utility extensions for managing [Flow] and [StateFlow]
 * state within a Compose-based UI.
 *
 * This class streamlines the conversion of cold flows into hot [StateFlow]s using a
 * consistent [SharingStarted.WhileSubscribed] strategy with a 5-second timeout, ensuring
 * that data collection is efficiently paused when the UI is no longer visible while
 * surviving configuration changes.
 */
abstract class ComposeViewModel : ViewModel() {

    /**
     * Converts a [Flow] into a [StateFlow] that is active only while there are active subscribers.
     *
     * Uses [SharingStarted.WhileSubscribed] with a 5-second stop timeout to ensure that
     * collection continues during configuration changes (like rotation) but stops when the
     * UI is no longer visible.
     *
     * @param default Default value of the state flow.
     * @return A [StateFlow] managed within the [viewModelScope].
     */
    protected fun <T> Flow<T>.stateWhileSubscribed(default: T) =
        stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), default)

    /**
     * Converts a [Flow] into a [StateFlow] that is active only while there are active subscribers.
     *
     * Uses [SharingStarted.WhileSubscribed] with a 5-second stop timeout to ensure that
     * collection continues during configuration changes (like rotation) but stops when the
     * UI is no longer visible.
     *
     * @return A [StateFlow] managed within the [viewModelScope].
     */
    protected fun <T> StateFlow<T>.stateWhileSubscribed() =
        stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), value)

}
