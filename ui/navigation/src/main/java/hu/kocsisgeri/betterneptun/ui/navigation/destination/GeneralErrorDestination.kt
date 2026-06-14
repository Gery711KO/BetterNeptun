package hu.kocsisgeri.betterneptun.ui.navigation.destination

import androidx.navigation3.runtime.NavKey
import hu.kocsisgeri.betterneptun.domain.error.model.ErrorContent
import hu.kocsisgeri.betterneptun.ui.navigation.transition.ErrorTransition
import kotlinx.serialization.Serializable

@Serializable
data object GeneralErrorDestination: NavKey, ErrorTransition {
}