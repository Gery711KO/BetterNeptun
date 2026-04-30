package hu.kocsisgeri.betterneptun.di

import hu.kocsisgeri.betterneptun.notification.NotificationScheduler
import org.koin.dsl.module

val notificationModule = module {
    single { NotificationScheduler(get()) }
}
