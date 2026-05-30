package hu.kocsisgeri.betterneptun.data.di

import hu.kocsisgeri.betterneptun.data.datasource.LocalDataSource
import hu.kocsisgeri.betterneptun.data.datasource.LocalDataSourceImpl
import hu.kocsisgeri.betterneptun.data.datasource.LocalizationDataSource
import hu.kocsisgeri.betterneptun.data.datasource.LocalizationDataSourceImpl
import hu.kocsisgeri.betterneptun.data.datasource.NetworkDataSource
import hu.kocsisgeri.betterneptun.data.datasource.NetworkDataSourceImpl
import hu.kocsisgeri.betterneptun.data.repository.localization.LocalizationRepositoryImpl
import hu.kocsisgeri.betterneptun.data.repository.login.LoginRepositoryImpl
import hu.kocsisgeri.betterneptun.data.token.TokenManagerImpl
import hu.kocsisgeri.betterneptun.domain.repository.localization.LocalizationRepository
import hu.kocsisgeri.betterneptun.domain.repository.login.LoginRepository
import hu.kocsisgeri.betterneptun.domain.token.TokenManager
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.dsl.new
import org.koin.dsl.module

val persistentDataModule = module {
    single { Dispatchers.IO }
    single<NetworkDataSource> { new(::NetworkDataSourceImpl) }

    single<LocalDataSource> { new(::LocalDataSourceImpl) }
    single<LocalizationDataSource> { new(::LocalizationDataSourceImpl) }

    single<TokenManager> { new(::TokenManagerImpl) }

    single<LoginRepository> { new(::LoginRepositoryImpl) }
    single<LocalizationRepository> { new(::LocalizationRepositoryImpl) }
}