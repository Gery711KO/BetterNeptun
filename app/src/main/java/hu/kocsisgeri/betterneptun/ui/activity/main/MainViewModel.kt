package hu.kocsisgeri.betterneptun.ui.activity.main

import hu.kocsisgeri.betterneptun.domain.model.ThemeMode
import hu.kocsisgeri.betterneptun.domain.usecase.settings.GetStoredThemeUseCase
import hu.kocsisgeri.betterneptun.ui.core.ComposeViewModel
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class MainViewModel(
    getTheme: GetStoredThemeUseCase,
) : ComposeViewModel() {

    val themeMode = getTheme().stateWhileSubscribed(ThemeMode.AUTO)
}
