package hu.kocsisgeri.betterneptun.ui.di

import hu.kocsisgeri.betterneptun.ui.screen.home.HomeViewModel
import hu.kocsisgeri.betterneptun.ui.screen.login.LoginViewModel
import hu.kocsisgeri.betterneptun.ui.screen.messages.MessagesViewModel
import hu.kocsisgeri.betterneptun.ui.screen.messages.detail.MessageDetailViewModel
import hu.kocsisgeri.betterneptun.ui.screen.semesters.SemestersViewModel
import hu.kocsisgeri.betterneptun.ui.screen.settings.SettingsViewModel
import hu.kocsisgeri.betterneptun.ui.screen.subjects.SubjectsViewModel
import hu.kocsisgeri.betterneptun.ui.screen.timetable.TimetableViewModel
import hu.kocsisgeri.betterneptun.ui.core.helper.ClockTickReceiver
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val uiModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::MessagesViewModel)
    viewModelOf(::MessageDetailViewModel)
    viewModelOf(::TimetableViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::SubjectsViewModel)
    viewModelOf(::SemestersViewModel)

    factoryOf(::ClockTickReceiver)
}