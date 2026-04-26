package hu.kocsisgeri.betterneptun.ui.model

import androidx.navigation3.runtime.NavKey

interface InteractionEvent
data class NavigationEvent(val destination: NavKey): InteractionEvent
data class ReadMessageEvent(val messageId: String) : InteractionEvent