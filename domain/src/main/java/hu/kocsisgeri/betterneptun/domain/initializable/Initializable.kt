package hu.kocsisgeri.betterneptun.domain.initializable

import kotlinx.coroutines.flow.StateFlow

interface Initializable {

    val isInitialized: StateFlow<Boolean>

    suspend fun initialize()
}