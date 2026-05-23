package hu.kocsisgeri.betterneptun.di

import hu.kocsisgeri.betterneptun.domain.initializable.Initializable
import hu.kocsisgeri.betterneptun.domain.initializable.Initializer
import hu.kocsisgeri.betterneptun.initialization.InitializerImpl
import hu.kocsisgeri.betterneptun.notification.NotificationScheduler
import org.koin.dsl.module

val appModule = module {
    single { NotificationScheduler(get()) }
    single<Initializer> { InitializerImpl(getAll<Initializable>()) }
}
