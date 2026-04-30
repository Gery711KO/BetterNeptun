package hu.kocsisgeri.betterneptun.ui.permission

import android.app.Activity
import hu.kocsisgeri.betterneptun.ui.permission.model.PermissionData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface PermissionHandler {

    val permissions: StateFlow<List<PermissionData>>

    fun getPermissions(activity: Activity): Flow<List<PermissionData>>

    fun refreshPermissions()
}