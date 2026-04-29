package hu.kocsisgeri.betterneptun.ui.screen.messages

import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.common.launchReportingErrors
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.ui.base.ComposeViewModel

class MessagesViewModel(
    private val neptunRepository: NeptunRepository
) : ComposeViewModel() {

    val listItems = neptunRepository.messages
        .stateWhileSubscribed()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launchReportingErrors {
            neptunRepository.fetchMessages()
        }
    }
}