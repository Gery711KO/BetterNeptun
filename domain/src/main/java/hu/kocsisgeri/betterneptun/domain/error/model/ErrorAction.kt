package hu.kocsisgeri.betterneptun.domain.error.model

sealed interface ErrorAction {

    val label: String

    data class Suspend(
        override val label: String,
        val onSuccess: PredefinedAction = PredefinedAction.NavigateBack,
        val action: suspend () -> Boolean
    ): ErrorAction

    data class Normal(
        override val label: String,
        val action: PredefinedAction
    ): ErrorAction

    enum class PredefinedAction {

        NavigateBack,
        NavigateToLoading,
        NavigateBackToHome,
        PopThenNavigateBackToDestinationBeforeCurrent,
    }
}
