package hu.kocsisgeri.betterneptun.ui.navigation.destination

import androidx.navigation3.runtime.NavKey
import hu.kocsisgeri.betterneptun.ui.navigation.transition.SharedBoundsTransition
import hu.kocsisgeri.betterneptun.ui.navigation.transition.SplashTransition
import kotlinx.serialization.Serializable

@Serializable
data object HomeDestination: NavKey, SharedBoundsTransition, SplashTransition
