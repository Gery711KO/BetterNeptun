package hu.kocsisgeri.betterneptun.notification

import android.content.Context
import hu.kocsisgeri.betterneptun.domain.initializable.Initializable
import hu.kocsisgeri.betterneptun.domain.model.neptun.CalendarItem
import hu.kocsisgeri.betterneptun.domain.repository.neptun.CalendarRepository
import hu.kocsisgeri.betterneptun.domain.repository.settings.SettingsRepository
import hu.kocsisgeri.betterneptun.ui.core.permission.PermissionHandler
import hu.kocsisgeri.betterneptun.ui.core.permission.model.PermissionData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.annotation.Singleton

@Singleton
class NotificationController(
    private val context: Context,
    private val notificationScheduler: NotificationScheduler,
    private val calendarRepository: CalendarRepository,
    private val settingsRepository: SettingsRepository,
    private val permissionHandler: PermissionHandler,
    private val scope: CoroutineScope,
) : Initializable {

    override suspend fun initialize(): Boolean {
        observeNotificationRequirements()
        return true
    }

    private fun observeNotificationRequirements() {
        combine(
            calendarRepository.events,
            settingsRepository.notificationDelay,
            permissionHandler.permissions,
        ) { events, delay, permissions ->
            NotificationRequirements(
                events = events,
                delay = delay,
                allPermissionsGranted = permissions.isNotEmpty() && permissions.all { it.permissionState == PermissionData.State.Granted }
            )
        }.onEach { requirements ->
            if (requirements.allPermissionsGranted) {
                if (requirements.delay != -1) {
                    requirements.events.forEach { event ->
                        notificationScheduler.scheduleNotification(
                            context = context,
                            item = event,
                            delayMinutes = requirements.delay
                        )
                    }
                } else {
                    requirements.events.forEach { event ->
                        notificationScheduler.cancelNotification(
                            context = context,
                            itemId = event.id
                        )
                    }
                }
            }
        }.launchIn(scope)
    }

    private data class NotificationRequirements(
        val events: List<CalendarItem>,
        val delay: Int,
        val allPermissionsGranted: Boolean
    )
}
