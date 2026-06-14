package hu.kocsisgeri.betterneptun.ui.core.permission.model

import android.app.Activity
import android.content.Context
import androidx.activity.compose.ManagedActivityResultLauncher

/**
 * Abstract base class representing the data and logic associated with a specific Android permission.
 * It encapsulates the permission's current state, the system permission string, and the disclaimer
 * to be shown to the user.
 */
abstract class PermissionData {

    /**
     * The unique identifier used to persist whether this specific [permission]
     * has been requested for the first time.
     */
    protected val isFirstTimeAskingPermission = "first_time_asking_$permission"

    val dismissedUntil = "dismissed_until_$permission"

    /**
     * The specific Android manifest permission string (e.g., [android.Manifest.permission.POST_NOTIFICATIONS]).
     */
    abstract val permission: String

    /**
     * The current authorization status of the associated [permission].
     */
    abstract val permissionState: State

    /**
     * The visual information and rationale to be displayed to the user when requesting this permission.
     */
    abstract val disclaimer: PermissionDisclaimer

    /**
     * Requests the permission from the user using the provided [launcher].
     *
     * @param context The [Context] used to handle permission-related operations.
     * @param launcher The [ManagedActivityResultLauncher] used to trigger the system permission dialog.
     */
    abstract fun requestPermission(
        context: Context,
        launcher: ManagedActivityResultLauncher<String, Boolean>
    )

    /**
     * Updates the [permissionState] based on the current status of the [permission] in the given [activity].
     * This method is responsible for checking the system permission status and updating the internal
     * state to [State.Granted], [State.Denied], [State.PermanentlyDenied], or [State.NotRequested].
     *
     * @param activity The current activity context used to check the permission status and rationale.
     */
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
