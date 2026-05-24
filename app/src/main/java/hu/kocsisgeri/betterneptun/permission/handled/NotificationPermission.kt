package hu.kocsisgeri.betterneptun.permission.handled

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import hu.kocsisgeri.betterneptun.data.util.get
import hu.kocsisgeri.betterneptun.data.util.put
import hu.kocsisgeri.betterneptun.localization.LocalizationKey
import hu.kocsisgeri.betterneptun.ui.core.permission.model.PermissionData
import hu.kocsisgeri.betterneptun.ui.core.permission.model.PermissionDisclaimer
import org.koin.core.annotation.Singleton

@Singleton
class NotificationPermission(
    private val sharedPreferences: SharedPreferences,
) : PermissionData() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override val permission: String = Manifest.permission.POST_NOTIFICATIONS
    override var permissionState: State by mutableStateOf(State.NotRequested)

    override val disclaimer: PermissionDisclaimer = PermissionDisclaimer(
        humanReadablePermissionName = LocalizationKey.PERMISSION_NOTIFICATION_TITLE,
        disclaimer = LocalizationKey.PERMISSION_NOTIFICATION_DISCLAIMER
    )

    override fun requestPermission(
        context: Context,
        launcher: ManagedActivityResultLauncher<String, Boolean>
    ) {
        sharedPreferences.put(cacheKey, false)

        when (permissionState) {
            State.Granted -> {
                // do not request
            }

            State.Denied -> launcher.handleLaunch(context)
            State.NotRequested -> launcher.handleLaunch(context)
            State.PermanentlyDenied -> context.openSettings()
        }
    }

    override fun refreshPermissionState(activity: Activity) {
        permissionState = getCurrentPermissionState(activity)
    }

    private fun getCurrentPermissionState(activity: Activity) = run {
        val hasPermission = NotificationManagerCompat.from(activity)
            .areNotificationsEnabled()

        val showRationale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat
                .shouldShowRequestPermissionRationale(activity, permission)
        } else {
            false
        }

        val isFirstTimeAskingPermission = sharedPreferences.get(
            key = cacheKey,
            defaultValue = false
        )

        if (hasPermission) State.Granted
        else {
            if (isFirstTimeAskingPermission) {
                State.NotRequested
            } else {
                if (showRationale) State.Denied
                else State.PermanentlyDenied
            }
        }
    }

    private fun ManagedActivityResultLauncher<String, Boolean>.handleLaunch(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launch(permission)
        } else {
            context.openSettings()
        }
    }

    private fun Context.openSettings() {
        try {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            }
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Fallback: Open general settings or show a message
            val intent = Intent(Settings.ACTION_SETTINGS)
            startActivity(intent)
        }
    }
}