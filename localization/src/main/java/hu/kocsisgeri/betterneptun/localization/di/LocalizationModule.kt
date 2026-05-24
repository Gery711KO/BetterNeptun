package hu.kocsisgeri.betterneptun.localization.di

import android.content.Context
import hu.kocsisgeri.betterneptun.domain.initializable.Initializable
import hu.kocsisgeri.betterneptun.domain.service.LocalizationService
import hu.kocsisgeri.betterneptun.localization.LocalizationServiceImpl
import org.koin.dsl.bind
import org.koin.dsl.module

val localizationModule = module {
    single<LocalizationService> {
        LocalizationServiceImpl(
            context = get<Context>(),
            localizationRepository = get(),
            settingsRepository = get()
        )
    } bind Initializable::class
}