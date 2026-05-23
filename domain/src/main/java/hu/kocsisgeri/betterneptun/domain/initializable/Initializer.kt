package hu.kocsisgeri.betterneptun.domain.initializable

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface Initializer {

    val isInitialized: StateFlow<Boolean>

    fun initialize(scope: CoroutineScope)
}