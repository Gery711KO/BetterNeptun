package hu.kocsisgeri.betterneptun

import android.app.Application
import hu.kocsisgeri.betterneptun.core.database.di.databaseModule
import hu.kocsisgeri.betterneptun.core.network.di.networkModule
import hu.kocsisgeri.betterneptun.data.di.dataModule
import hu.kocsisgeri.betterneptun.di.appModule
import hu.kocsisgeri.betterneptun.di.permissionModule
import hu.kocsisgeri.betterneptun.domain.di.domainModule
import hu.kocsisgeri.betterneptun.ui.di.navigationModule
import hu.kocsisgeri.betterneptun.ui.di.uiModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import timber.log.Timber

open class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin()
        initLogging()
    }

    open fun startKoin() {
        startKoin {
            androidContext(this@MyApplication)
            modules(koinModules)
        }
    }

    open fun initLogging() {
        Timber.plant(Timber.DebugTree())
    }

    companion object {
        internal val koinModules = listOf(
            networkModule,
            databaseModule,
            dataModule,
            domainModule,
            navigationModule,
            uiModule,
            permissionModule,
            appModule,
        )
    }
}