package hu.kocsisgeri.betterneptun.ui.core.helper

import kotlinx.coroutines.flow.Flow

interface ClockMinutesTickReceiver {

    val minuteTick: Flow<Unit>
}
