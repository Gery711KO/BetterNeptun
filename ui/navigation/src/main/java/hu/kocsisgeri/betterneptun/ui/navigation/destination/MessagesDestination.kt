package hu.kocsisgeri.betterneptun.ui.navigation.destination

import androidx.navigation3.runtime.NavKey
import hu.kocsisgeri.betterneptun.ui.navigation.SharedBoundsTransition
import kotlinx.serialization.Serializable

@Serializable
data object MessagesDestination: NavKey, SharedBoundsTransition
