package hu.kocsisgeri.betterneptun.ui.core.permission

import android.content.Context
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import hu.kocsisgeri.betterneptun.ui.core.permission.model.PermissionData

@Composable
fun rememberPermissionLauncher(permissionHandler: PermissionHandler): PermissionHandler.Launcher {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        permissionHandler.refreshPermissions()
    }

    LifecycleResumeEffect(permissionHandler.permissions.collectAsStateWithLifecycle()) {
        permissionHandler.refreshPermissions()

        onPauseOrDispose {}
    }

    return remember(context, launcher) {
        LauncherImpl(context, launcher)
    }
}

private class LauncherImpl(
    private val context: Context,
    private val permissionLauncher: ManagedActivityResultLauncher<String, Boolean>
): PermissionHandler.Launcher {

    override fun launch(permissionData: PermissionData) {
        permissionData.requestPermission(context, permissionLauncher)
    }
}
