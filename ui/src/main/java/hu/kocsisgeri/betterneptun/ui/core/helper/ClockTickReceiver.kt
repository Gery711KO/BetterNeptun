package hu.kocsisgeri.betterneptun.ui.core.helper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ClockTickReceiver(context: Context) {

    val minuteTickFlow: Flow<Unit> = context.minuteTickFlow()

    private fun Context.minuteTickFlow(): Flow<Unit> = callbackFlow {
        trySend(Unit)

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                trySend(Unit) // Jelzés küldése minden perc váltásakor
            }
        }

        registerReceiver(receiver, IntentFilter(Intent.ACTION_TIME_TICK))

        awaitClose { unregisterReceiver(receiver) }
    }
}