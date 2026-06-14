package hu.kocsisgeri.betterneptun.ui.core.permission

import android.app.Activity
import hu.kocsisgeri.betterneptun.ui.core.permission.model.PermissionData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Interface responsible for managing and observing Android system permissions.
 * It provides a reactive way to track permission statuses and trigger updates.
 */
interface PermissionHandler {

    /**
     * A [StateFlow] emitting the current list of [PermissionData], representing
     * the real-time status and requirements of all handled permissions.
     */
    val permissions: StateFlow<List<PermissionData>>

    /**
     * Returns a [Flow] that emits the current status of all relevant permissions.
     *
     * @param activity The [Activity] context used to check the current permission states.
     * @return A [Flow] emitting a list of [PermissionData] objects representing the state of each permission.
     */
    fun getPermissions(activity: Activity): Flow<List<PermissionData>>

    /**
     * Re-evaluates the current status of all managed permissions and updates the [permissions] state flow.
     */
    fun refreshPermissions()

    /**
     * Interface responsible for launching the system permission request process.
     */
    interface Launcher {

        /**
         * Triggers the permission request process for the specified [permissionData].
         *
         * @param permissionData The model containing the permission information to be requested.
         */
        fun launch(permissionData: PermissionData)
    }
}
