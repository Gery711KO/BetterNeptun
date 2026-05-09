package hu.kocsisgeri.betterneptun.ui.screen.messages.detail

import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.ui.base.ComposeViewModel
import kotlinx.coroutines.flow.map

class MessageDetailViewModel(
    private val messageId: String,
    neptunRepository: NeptunRepository,
) : ComposeViewModel() {

    val message = neptunRepository.messages.map { pager ->
        pager.messages.find { it.id == messageId }
    }.stateWhileSubscribed(null)
}