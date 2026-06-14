package hu.kocsisgeri.betterneptun.ui.screen.messages

import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.common.utils.launchReportingErrors
import hu.kocsisgeri.betterneptun.domain.error.ErrorRegistry
import hu.kocsisgeri.betterneptun.domain.error.model.ErrorAction
import hu.kocsisgeri.betterneptun.domain.error.model.ErrorContent
import hu.kocsisgeri.betterneptun.domain.model.neptun.MessagesPager
import hu.kocsisgeri.betterneptun.domain.usecase.messages.GetMessagesPagerUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.messages.LoadMoreMessagesUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.messages.RefreshMessagesUseCase
import hu.kocsisgeri.betterneptun.ui.core.ErrorHandlingComposeViewModel
import hu.kocsisgeri.betterneptun.ui.designsystem.R
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class MessagesViewModel(
    private val getMessagesPagerUseCase: GetMessagesPagerUseCase,
    private val refreshMessagesUseCase: RefreshMessagesUseCase,
    private val loadMoreMessagesUseCase: LoadMoreMessagesUseCase,
    errorRegistry: ErrorRegistry,
) : ErrorHandlingComposeViewModel(errorRegistry) {

    val listItems = getMessagesPagerUseCase()
        .registerToGeneralErrors { pager ->
            if (pager.messages.isEmpty()) ErrorContent.FullScreen(
                icon = R.raw.error_lottie,
                title = "Fetch messages failed",
                description = "Something went wrong during message fetching, please try again.",
                primaryAction = ErrorAction.Suspend(
                    label = "Retry",
                    action = {
                        refreshMessagesUseCase(force = true)
                        getMessagesPagerUseCase().first().error == null
                    }
                ),
                secondaryAction = ErrorAction.Normal(
                    label = "Back to Home",
                    action = ErrorAction.PredefinedAction.NavigateBackToHome
                )
            ) else {
                ErrorContent.Snackbar(text = "Couldn't fetch more messages.")
            }.takeIf { pager.error != null }
        }
        .stateWhileSubscribed(MessagesPager())

    init {
        viewModelScope.launchReportingErrors {
            refreshMessagesUseCase()
        }
    }

    fun loadMore() {
        viewModelScope.launchReportingErrors {
            loadMoreMessagesUseCase()
        }
    }
}
