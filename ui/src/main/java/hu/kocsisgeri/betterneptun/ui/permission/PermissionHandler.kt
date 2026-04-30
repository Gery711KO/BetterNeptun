package hu.kocsisgeri.betterneptun.ui.permission

import android.app.Activity
import hu.kocsisgeri.betterneptun.ui.permission.model.PermissionData
import kotlinx.coroutines.flow.Flow

interface PermissionHandler {

    fun getPermissions(activity: Activity): Flow<List<PermissionData>>
}