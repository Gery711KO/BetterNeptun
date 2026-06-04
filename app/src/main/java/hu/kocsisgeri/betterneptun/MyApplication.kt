package hu.kocsisgeri.betterneptun

import android.app.Application
import hu.kocsisgeri.betterneptun.di.MyApp
import org.koin.android.ext.koin.androidContext
import org.koin.plugin.module.dsl.startKoin
import timber.log.Timber

open class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin()
        initLogging()
    }

    open fun startKoin() {
        startKoin<MyApp> {
            androidContext(this@MyApplication)
            printLogger()
        }

    }

    open fun initLogging() {
        Timber.plant(Timber.DebugTree())
    }
}