package hu.kocsisgeri.betterneptun.di

import android.content.Context
import hu.kocsisgeri.betterneptun.R
import hu.kocsisgeri.betterneptun.permission.PermissionHandlerImpl
import hu.kocsisgeri.betterneptun.permission.handled.BackgroundAlarm
import hu.kocsisgeri.betterneptun.permission.handled.NotificationPermission
import hu.kocsisgeri.betterneptun.ui.permission.PermissionHandler
import hu.kocsisgeri.betterneptun.ui.permission.model.PermissionData
import org.koin.dsl.bind
import org.koin.dsl.module

val permissionModule = module {
    single {
        BackgroundAlarm(appName = get<Context>().getString(R.string.app_name))
    } bind PermissionData::class

    single {
        NotificationPermission(sharedPreferences = get(),)
    } bind PermissionData::class

    single<PermissionHandler> {
        PermissionHandlerImpl(
            handledPermissions = getAll<PermissionData>()
        )
    }
}