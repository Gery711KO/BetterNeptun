package hu.kocsisgeri.betterneptun.ui.screen.messages

import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.common.launchReportingErrors
import hu.kocsisgeri.betterneptun.domain.model.ApiResult
import hu.kocsisgeri.betterneptun.domain.model.MessagesPager
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.ui.core.ComposeViewModel
import kotlinx.coroutines.flow.map

class MessagesViewModel(
    private val neptunRepository: NeptunRepository
) : ComposeViewModel() {

    val listItems = neptunRepository.messages
        .map { pager ->
            pager.copy(messages = pager.messages.distinctBy { it.id })
        }
        .stateWhileSubscribed(MessagesPager())

    init {
        refresh()
    }

    fun refresh() {
        if (neptunRepository.messages.value.messages.isEmpty()) {
            viewModelScope.launchReportingErrors {
                neptunRepository.fetchMessages(isRefresh = true)
            }
        } else {
            viewModelScope.launchReportingErrors {
                neptunRepository.checkForMessageUpdates()
            }
        }
    }

    fun loadMore() {
        if (listItems.value.isLoadingNextMessages) return
        
        viewModelScope.launchReportingErrors {
            neptunRepository.fetchMessages(isRefresh = false)
        }
    }
}