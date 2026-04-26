package hu.kocsisgeri.betterneptun.ui.di

import hu.kocsisgeri.betterneptun.ui.screen.home.HomeViewModel
import hu.kocsisgeri.betterneptun.ui.screen.login.LoginViewModel
import hu.kocsisgeri.betterneptun.ui.screen.messages.MessagesViewModel
import hu.kocsisgeri.betterneptun.ui.screen.semesters.SemestersViewModel
import hu.kocsisgeri.betterneptun.ui.screen.settings.SettingsViewModel
import hu.kocsisgeri.betterneptun.ui.activity.splash.SplashViewModel
import hu.kocsisgeri.betterneptun.ui.screen.subjects.SubjectsViewModel
import hu.kocsisgeri.betterneptun.ui.screen.timetable.TimetableViewModel
import hu.kocsisgeri.betterneptun.data.datamanager.DataManager
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { SplashViewModel() }

    single { DataManager(get(), get(), get(), get()) }

    viewModel { LoginViewModel(get(), get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { MessagesViewModel(get()) }
    viewModel { TimetableViewModel(get()) }
    viewModel { SettingsViewModel(get(), get()) }
    viewModel { SubjectsViewModel(get()) }
    viewModel { SemestersViewModel(get()) }
}