package hu.kocsisgeri.betterneptun.domain.initializable

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface Initializer {

    val isInitialized: StateFlow<State>

    fun initialize(scope: CoroutineScope)

    sealed interface State {

        data object Idle: State
        data object Initialized: State
        data object Initializing: State
        data class Error(val errorMessage: String): State
    }
}