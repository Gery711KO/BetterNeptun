package hu.kocsisgeri.betterneptun.domain.di

import hu.kocsisgeri.betterneptun.domain.usecase.home.FetchUnreadMessagesUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.home.GetCurrentCoursesUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.home.GetNextCourseUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.home.GetStudentDataUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.auth.LoginUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.auth.LogoutUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.auth.SilentLoginUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.messages.GetMessageDetailUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.messages.GetMessagesPagerUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.messages.LoadMoreMessagesUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.messages.RefreshMessagesUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.semester.FetchTermAveragesUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.semester.FetchTermsUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.semester.GetSemesterAveragesUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.semester.GetSemesterCreditsUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.settings.GetStoredNotificationDelayUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.settings.GetStoredThemeUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.settings.SaveSettingsUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.subjects.FetchSubjectsUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.subjects.GetSubjectsUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.subjects.GetTermsUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.timetable.AddLocalEventUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.timetable.DeleteLocalEventUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.timetable.GetEventsUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {

    factoryOf(::LoginUseCase)
    factoryOf(::SilentLoginUseCase)
    factoryOf(::LogoutUseCase)

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

    factoryOf(::FetchTermsUseCase)
    factoryOf(::FetchTermAveragesUseCase)
    factoryOf(::GetSemesterAveragesUseCase)
    factoryOf(::GetSemesterCreditsUseCase)

    factoryOf(::FetchSubjectsUseCase)
    factoryOf(::GetSubjectsUseCase)
    factoryOf(::GetTermsUseCase)

    factoryOf(::GetEventsUseCase)
    factoryOf(::AddLocalEventUseCase)
    factoryOf(::DeleteLocalEventUseCase)
}
