package hu.kocsisgeri.betterneptun.domain.di

import hu.kocsisgeri.betterneptun.domain.usecase.home.FetchUnreadMessagesUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.home.GetCurrentCoursesUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.home.GetNextCourseUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.home.GetStudentDataUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.login.LogOutUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.login.LoginUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.login.SilentLoginUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.messages.GetMessageDetailUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.messages.GetMessagesPagerUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.messages.LoadMoreMessagesUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.messages.RefreshMessagesUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.settings.GetStoredNotificationDelayUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.settings.GetStoredThemeUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.settings.SaveSettingsUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {

    factoryOf(::LogOutUseCase)
    factoryOf(::LoginUseCase)
    factoryOf(::SilentLoginUseCase)

    factoryOf(::FetchUnreadMessagesUseCase)
    factoryOf(::GetCurrentCoursesUseCase)
    factoryOf(::GetNextCourseUseCase)
    factoryOf(::GetStudentDataUseCase)

    factoryOf(::GetMessageDetailUseCase)
    factoryOf(::GetMessagesPagerUseCase)
    factoryOf(::LoadMoreMessagesUseCase)
    factoryOf(::RefreshMessagesUseCase)

    factoryOf(::SaveSettingsUseCase)
    factoryOf(::GetStoredThemeUseCase)
    factoryOf(::GetStoredNotificationDelayUseCase)
}
