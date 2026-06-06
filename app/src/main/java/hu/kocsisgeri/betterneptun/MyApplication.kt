package hu.kocsisgeri.betterneptun

import android.app.Application
import hu.kocsisgeri.betterneptun.di.App
import org.koin.android.ext.koin.androidContext
import org.koin.plugin.module.dsl.startKoin
import timber.log.Timber

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin()
        initLogging()
    }

    fun startKoin() {
        startKoin<App> {
            androidContext(this@MyApplication)
            printLogger()
        }
    }

    fun initLogging() {
        Timber.plant(Timber.DebugTree())
    }
}
