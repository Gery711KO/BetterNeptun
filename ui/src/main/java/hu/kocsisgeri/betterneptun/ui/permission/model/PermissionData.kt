package hu.kocsisgeri.betterneptun.ui.permission.model

import android.app.Activity
import android.content.Context
import androidx.activity.compose.ManagedActivityResultLauncher

abstract class PermissionData {

    protected val cacheKey = "first_time_asking_$permission"

    abstract val permission: String
    abstract val permissionState: State
    abstract val disclaimer: PermissionDisclaimer

    abstract fun requestPermission(
        context: Context,
        launcher: ManagedActivityResultLauncher<String, Boolean>
    )

    abstract fun refreshPermissionState(activity: Activity)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PermissionData) return false

        return permission == other.permission &&
                permissionState == other.permissionState &&
                disclaimer == other.disclaimer
    }

    override fun hashCode(): Int {
        var result = permission.hashCode()
        result = 31 * result + permissionState.hashCode()
        result = 31 * result + disclaimer.hashCode()

        return result
    }

    enum class State {
        Granted, Denied, PermanentlyDenied, NotRequested
    }
}