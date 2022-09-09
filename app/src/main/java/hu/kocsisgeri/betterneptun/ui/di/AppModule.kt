package hu.kocsisgeri.betterneptun.ui.di

import hu.kocsisgeri.betterneptun.ui.home.HomeViewModel
import hu.kocsisgeri.betterneptun.ui.login.LoginViewModel
import hu.kocsisgeri.betterneptun.ui.messages.MessagesViewModel
import hu.kocsisgeri.betterneptun.ui.settings.SettingsViewModel
import hu.kocsisgeri.betterneptun.ui.splash.SplashViewModel
import hu.kocsisgeri.betterneptun.ui.timetable.TimetableViewModel
import hu.kocsisgeri.betterneptun.utils.data_manager.DataManager
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { SplashViewModel() }

    single { DataManager(get(), get(), get()) }

    viewModel { LoginViewModel(networkDataSource = get(), get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { MessagesViewModel(get()) }
    viewModel { TimetableViewModel(get()) }
    viewModel { SettingsViewModel(get(), get()) }
}