package hu.kocsisgeri.betterneptun.permission.handled

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import hu.kocsisgeri.betterneptun.R
import hu.kocsisgeri.betterneptun.ui.core.permission.model.PermissionData
import hu.kocsisgeri.betterneptun.ui.core.permission.model.PermissionDisclaimer
import org.koin.core.annotation.Singleton

@Singleton
class BackgroundAlarm(context: Context): PermissionData() {

    override val permission: String = Manifest.permission.SCHEDULE_EXACT_ALARM
    override var permissionState : State by mutableStateOf(State.NotRequested)

    override val disclaimer: PermissionDisclaimer = PermissionDisclaimer(
        humanReadablePermissionName = "Háttérműveletek",
        disclaimer = "A háttérműveletek bekapcsolása az értesítések megfelelő működéséhez szükséges, " +
                "ugyanis az értesítések teljesen lokálisan szerver nélkül kerülnek elküldésre.\n" +
                "A felugró ablakban keresd ki az ${
                    context.getString(R.string.app_name)
                } appot és engedélyezd a háttérműveleteket."
    )

    override fun requestPermission(
        context: Context,
        launcher: ManagedActivityResultLauncher<String, Boolean>
    ) {
        if (permissionState == State.PermanentlyDenied) {
            context.startActivity(
                Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    Uri.fromParts("package", context.packageName, null)
                }
            )
        } else {
            // Do nothing we have permission
        }
    }

    override fun refreshPermissionState(activity: Activity) {
        permissionState = getCurrentPermissionState(activity)
    }

    private fun getCurrentPermissionState(activity: Activity) = run {
        val alarmManager = (activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager)

        if (alarmManager.canScheduleExactAlarms()) State.Granted
        else State.PermanentlyDenied
    }
}