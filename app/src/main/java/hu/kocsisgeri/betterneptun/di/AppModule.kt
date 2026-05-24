package hu.kocsisgeri.betterneptun.di

import hu.kocsisgeri.betterneptun.broadcast.ClockMinutesTickReceiverImpl
import hu.kocsisgeri.betterneptun.domain.initializable.Initializable
import hu.kocsisgeri.betterneptun.domain.initializable.Initializer
import hu.kocsisgeri.betterneptun.initialization.InitializerImpl
import hu.kocsisgeri.betterneptun.notification.NotificationScheduler
import hu.kocsisgeri.betterneptun.ui.core.helper.ClockMinutesTickReceiver
import org.koin.dsl.module

val appModule = module {
    single { NotificationScheduler() }
    single<Initializer> { InitializerImpl(getAll<Initializable>()) }

    factory<ClockMinutesTickReceiver> { ClockMinutesTickReceiverImpl(get()) }
}
