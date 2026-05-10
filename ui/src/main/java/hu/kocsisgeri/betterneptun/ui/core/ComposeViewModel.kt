package hu.kocsisgeri.betterneptun.ui.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

abstract class ComposeViewModel : ViewModel() {

    protected fun <T> Flow<T>.stateWhileSubscribed(default: T) =
        stateIn(viewModelScope, SharingStarted.Companion.WhileSubscribed(5000L), default)

    protected fun <T> StateFlow<T>.stateWhileSubscribed() =
        stateIn(viewModelScope, SharingStarted.Companion.WhileSubscribed(5000L), value)
}