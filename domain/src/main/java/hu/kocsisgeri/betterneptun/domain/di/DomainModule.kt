package hu.kocsisgeri.betterneptun.domain.di

import android.content.Context
import hu.kocsisgeri.betterneptun.domain.initializable.Initializable
import hu.kocsisgeri.betterneptun.domain.service.LocalizationService
import hu.kocsisgeri.betterneptun.domain.service.LocalizationServiceImpl
import hu.kocsisgeri.betterneptun.domain.usecase.LogOutUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val domainModule = module {

    factoryOf(::LogOutUseCase)

    single<LocalizationService> {
        LocalizationServiceImpl(
            context = get<Context>(),
            localizationRepository = get()
        )
    } bind Initializable::class
}