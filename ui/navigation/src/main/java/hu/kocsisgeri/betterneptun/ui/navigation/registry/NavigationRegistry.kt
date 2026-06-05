package hu.kocsisgeri.betterneptun.ui.navigation.registry

import org.koin.core.annotation.Single

@Single
class NavigationRegistry(
    val navigationEntries: List<NavigationEntry>
)
