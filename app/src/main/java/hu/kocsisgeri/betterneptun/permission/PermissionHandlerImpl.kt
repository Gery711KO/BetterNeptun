package hu.kocsisgeri.betterneptun.permission

import android.app.Activity
import hu.kocsisgeri.betterneptun.ui.permission.PermissionHandler
import hu.kocsisgeri.betterneptun.ui.permission.model.PermissionData
import kotlinx.coroutines.flow.flowOf

class PermissionHandlerImpl(
    private val handledPermissions: List<PermissionData>,
): PermissionHandler {

    override fun getPermissions(activity: Activity) = flowOf(
        handledPermissions.map {
            it.apply { refreshPermissionState(activity) }
        }
    )
}