package hu.kocsisgeri.betterneptun.domain.di

import hu.kocsisgeri.betterneptun.domain.usecase.LogOutUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {

    factoryOf(::LogOutUseCase)
}