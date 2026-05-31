package hu.kocsisgeri.betterneptun.ui.destination

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class TimetableDestination(val selected: Long? = null): NavKey
