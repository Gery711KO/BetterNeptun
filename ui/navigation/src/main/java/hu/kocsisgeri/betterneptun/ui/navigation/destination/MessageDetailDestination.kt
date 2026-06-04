package hu.kocsisgeri.betterneptun.ui.navigation.destination

import androidx.navigation3.runtime.NavKey
import hu.kocsisgeri.betterneptun.ui.navigation.SlideTransition
import kotlinx.serialization.Serializable

@Serializable
data class MessageDetailDestination(val messageId: String): NavKey, SlideTransition
