package hu.kocsisgeri.betterneptun.ui.permission.model

import android.app.Activity
import android.content.Context
import androidx.activity.compose.ManagedActivityResultLauncher

interface PermissionData {

    val permission: String
    val permissionState: State?
    val disclaimer: PermissionDisclaimer

    fun requestPermission(
        activity: Activity,
        launcher: ManagedActivityResultLauncher<String, Boolean>
    )

    fun refreshPermissionState(activity: Activity)

    fun cacheKey() = "first_time_asking_$permission"

    enum class State {
        Granted, Denied, PermanentlyDenied, NotRequested
    }
}