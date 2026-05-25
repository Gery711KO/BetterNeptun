package hu.kocsisgeri.betterneptun.localization.di

import hu.kocsisgeri.betterneptun.domain.initializable.Initializable
import hu.kocsisgeri.betterneptun.domain.service.LocalizationService
import hu.kocsisgeri.betterneptun.localization.service.LocalizationServiceImpl
import org.koin.core.module.dsl.new
import org.koin.dsl.binds
import org.koin.dsl.module

val localizationModule = module {
    single {
        new(::LocalizationServiceImpl)
    } binds arrayOf(
        LocalizationService::class,
        Initializable::class
    )
}
