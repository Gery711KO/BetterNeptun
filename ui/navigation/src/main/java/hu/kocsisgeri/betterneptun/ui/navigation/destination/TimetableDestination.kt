package hu.kocsisgeri.betterneptun.ui.navigation.destination

import androidx.navigation3.runtime.NavKey
import hu.kocsisgeri.betterneptun.ui.navigation.transition.SharedBoundsTransition
import kotlinx.serialization.Serializable

@Serializable
data class TimetableDestination(val selected: Long? = null): NavKey, SharedBoundsTransition
