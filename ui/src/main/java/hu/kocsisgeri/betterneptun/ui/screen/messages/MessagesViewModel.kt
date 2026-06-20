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
import hu.kocsisgeri.betterneptun.localization.LocalizationKey
import hu.kocsisgeri.betterneptun.ui.core.ErrorHandlingComposeViewModel
import hu.kocsisgeri.betterneptun.ui.designsystem.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class MessagesViewModel(
    private val getMessagesPagerUseCase: GetMessagesPagerUseCase,
    private val refreshMessagesUseCase: RefreshMessagesUseCase,
    private val loadMoreMessagesUseCase: LoadMoreMessagesUseCase,
    errorRegistry: ErrorRegistry,
) : ErrorHandlingComposeViewModel(errorRegistry) {

    private val _selectedMessageId = MutableStateFlow<String?>(null)
    val selectedMessageId = _selectedMessageId.asStateFlow()

    val listItems = getMessagesPagerUseCase()
        .registerToGeneralErrors { pager ->
            if (pager.messages.isEmpty()) ErrorContent.FullScreen(
                icon = R.raw.error_lottie,
                title = LocalizationKey.ERROR_FETCH_MESSAGES,
                description = LocalizationKey.ERROR_FETCH_DESCRIPTION,
                primaryAction = ErrorAction.Suspend(
                    label = LocalizationKey.ERROR_BUTTON_RETRY,
                    action = {
                        refreshMessagesUseCase(force = true)
                        getMessagesPagerUseCase().first().error == null
                    }
                ),
                secondaryAction = ErrorAction.Normal(
                    label = LocalizationKey.ERROR_BUTTON_BACKTOHOME,
                    action = ErrorAction.PredefinedAction.NavigateBackToHome
                )
            ) else {
                ErrorContent.Snackbar(text = LocalizationKey.ERROR_FETCH_LOADMORE)
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

    fun selectMessage(id: String?) {
        _selectedMessageId.value = id
    }
}
