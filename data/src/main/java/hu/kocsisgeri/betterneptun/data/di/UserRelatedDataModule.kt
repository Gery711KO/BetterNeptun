package hu.kocsisgeri.betterneptun.data.di

import hu.kocsisgeri.betterneptun.data.repository.neptun.NeptunRepositoryImpl
import hu.kocsisgeri.betterneptun.data.repository.settings.SettingsRepositoryImpl
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.domain.repository.settings.SettingsRepository
import org.koin.core.module.dsl.new
import org.koin.dsl.module

val userRelatedDataModule = module {
    single<NeptunRepository> { new(::NeptunRepositoryImpl) }
    single<SettingsRepository> { new(::SettingsRepositoryImpl) }
}
