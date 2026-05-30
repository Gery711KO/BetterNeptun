package hu.kocsisgeri.betterneptun.ui.di

import hu.kocsisgeri.betterneptun.ui.contract.OnLogoutNavigatorContract
import hu.kocsisgeri.betterneptun.ui.contract.OnLogoutPurgeContract
import org.koin.core.module.dsl.new
import org.koin.dsl.module

val contractsModule = module {
    single(createdAtStart = true) { new(::OnLogoutNavigatorContract) }
    single(createdAtStart = true) { new(::OnLogoutPurgeContract) }
}