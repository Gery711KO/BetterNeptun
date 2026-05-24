package hu.kocsisgeri.betterneptun.ui.screen.messages

import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.common.utils.launchReportingErrors
import hu.kocsisgeri.betterneptun.domain.model.neptun.MessagesPager
import hu.kocsisgeri.betterneptun.domain.usecase.messages.RefreshMessagesUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.messages.GetMessagesPagerUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.messages.LoadMoreMessagesUseCase
import hu.kocsisgeri.betterneptun.ui.core.ComposeViewModel

class MessagesViewModel(
    getMessagesPagerUseCase: GetMessagesPagerUseCase,
    private val refreshMessagesUseCase: RefreshMessagesUseCase,
    private val loadMoreMessagesUseCase: LoadMoreMessagesUseCase,
) : ComposeViewModel() {

    val listItems = getMessagesPagerUseCase().stateWhileSubscribed(MessagesPager())

    init {
        refresh()
    }

    fun refresh() {
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
