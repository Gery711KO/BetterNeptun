package hu.kocsisgeri.betterneptun.domain.initializable

import kotlinx.coroutines.flow.SharedFlow

interface Initializable {

    val isInitialized: SharedFlow<Boolean>

    suspend fun initialize()
}
