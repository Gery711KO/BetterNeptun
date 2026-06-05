package hu.kocsisgeri.betterneptun.ui.navigation.registry

import androidx.navigation3.runtime.NavKey
import org.koin.core.annotation.Single

@Single
class NavigationRegistry(
    val destinations: List<Destination<NavKey>>
)
