package hu.kocsisgeri.betterneptun.domain.error.model

import hu.kocsisgeri.betterneptun.domain.service.Localization

sealed interface ErrorAction {

    val label: Localization

    data class Suspend(
        override val label: Localization,
        val onSuccess: PredefinedAction = PredefinedAction.NavigateBack,
        val action: suspend () -> Boolean
    ): ErrorAction

    data class Normal(
        override val label: Localization,
        val action: PredefinedAction
    ): ErrorAction

    enum class PredefinedAction {

        NavigateBack,
        NavigateToLoading,
        NavigateBackToHome,
        PopThenNavigateBackToDestinationBeforeCurrent,
    }
}
