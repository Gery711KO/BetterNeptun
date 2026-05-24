package hu.kocsisgeri.betterneptun.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.domain.repository.settings.SettingsRepository
import hu.kocsisgeri.betterneptun.notification.NotificationScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BootReceiver : BroadcastReceiver(), KoinComponent {

    private val neptunRepository: NeptunRepository by inject()
    private val settingsRepository: SettingsRepository by inject()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val scheduler = NotificationScheduler()

            CoroutineScope(Dispatchers.IO).launch {
                // TODO fetch the events from network to handle those as well

                val events = neptunRepository.events.first()
                val delay = settingsRepository.notificationDelay.first()

                events.forEach { event ->
                    scheduler.scheduleNotification(context, event, delay)
                }
            }
        }
    }
}