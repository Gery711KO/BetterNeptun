package hu.kocsisgeri.betterneptun.ui.di

import hu.kocsisgeri.betterneptun.ui.screen.home.HomeViewModel
import hu.kocsisgeri.betterneptun.ui.screen.login.LoginViewModel
import hu.kocsisgeri.betterneptun.ui.screen.messages.MessagesViewModel
import hu.kocsisgeri.betterneptun.ui.screen.messages.detail.MessageDetailViewModel
import hu.kocsisgeri.betterneptun.ui.screen.semesters.SemestersViewModel
import hu.kocsisgeri.betterneptun.ui.screen.settings.SettingsViewModel
import hu.kocsisgeri.betterneptun.ui.screen.subjects.SubjectsViewModel
import hu.kocsisgeri.betterneptun.ui.screen.timetable.TimetableViewModel
import hu.kocsisgeri.betterneptun.ui.screen.loading.LoadingViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel

val uiModule = module {
    viewModelOf(::LoadingViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::MessagesViewModel)
    viewModelOf(::MessageDetailViewModel)
    viewModel {
        TimetableViewModel(
            defaultSelected = it.getOrNull(),
            getEventsUseCase = get(),
            addLocalEventsUseCase = get(),
            deleteLocalEventUseCase = get(),
        )
    }
    viewModelOf(::SettingsViewModel)
    viewModelOf(::SubjectsViewModel)
    viewModelOf(::SemestersViewModel)
}
