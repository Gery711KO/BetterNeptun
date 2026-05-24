package hu.kocsisgeri.betterneptun.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import hu.kocsisgeri.betterneptun.broadcast.NotificationReceiver
import hu.kocsisgeri.betterneptun.domain.model.neptun.CalendarItem
import timber.log.Timber
import java.time.ZoneId
import java.util.Date

class NotificationScheduler {

    fun scheduleNotification(context: Context, item: CalendarItem, delayMinutes: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val triggerTime = item.startTime
            .minusMinutes(delayMinutes.toLong())
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        if (triggerTime <= System.currentTimeMillis()) return

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra(NotificationReceiver.EXTRA_TITLE, item.title)
            putExtra(NotificationReceiver.EXTRA_MESSAGE, "${item.location ?: ""} - ${item.startTime.toLocalTime()}")
            putExtra(NotificationReceiver.EXTRA_ID, item.id)
        }

        if (alarmManager.canScheduleExactAlarms()) {
            if (context.isAlarmAlreadySet(intent, item.id).not()) {
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    item.id.toInt(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                Timber.tag("Alarm").d("Alarm set for ${item.title} at ${Date(triggerTime)}")
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                Timber.tag("Alarm").d("Alarm with id: [${item.id}] is already set.")
            }
        } else {
            Timber.tag("Alarm").d("Alarm could not be set.")
        }
    }

    fun cancelNotification(context: Context, itemId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            itemId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }

    private fun Context.isAlarmAlreadySet(intent: Intent, itemId: Long): Boolean {
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            itemId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        return pendingIntent != null
    }
}
