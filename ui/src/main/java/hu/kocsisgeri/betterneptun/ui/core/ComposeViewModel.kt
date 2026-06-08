package hu.kocsisgeri.betterneptun.ui.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

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

    protected fun <T> Flow<T>.stateWhileSubscribed(default: T) =
        stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), default)

    protected fun <T> StateFlow<T>.stateWhileSubscribed() =
        stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), value)
}
