package hu.kocsisgeri.betterneptun.di

import hu.kocsisgeri.betterneptun.broadcast.ClockMinutesTickReceiverImpl
import hu.kocsisgeri.betterneptun.domain.auth.LogoutRequester
import hu.kocsisgeri.betterneptun.domain.auth.LogoutRegistry
import hu.kocsisgeri.betterneptun.domain.auth.OnLogoutCallback
import hu.kocsisgeri.betterneptun.domain.initializable.Initializable
import hu.kocsisgeri.betterneptun.domain.initializable.Initializer
import hu.kocsisgeri.betterneptun.initialization.InitializerImpl
import hu.kocsisgeri.betterneptun.initialization.SessionManager
import hu.kocsisgeri.betterneptun.notification.NotificationScheduler
import hu.kocsisgeri.betterneptun.ui.core.helper.ClockMinutesTickReceiver
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single { SessionManager() }
    single<LogoutRequester> { get<SessionManager>() }
    single<LogoutRegistry> { get<SessionManager>() }
    single { NotificationScheduler() }
    single<Initializer> { InitializerImpl(getAll<Initializable>()) }

    factory<ClockMinutesTickReceiver> { ClockMinutesTickReceiverImpl(get()) }
}
