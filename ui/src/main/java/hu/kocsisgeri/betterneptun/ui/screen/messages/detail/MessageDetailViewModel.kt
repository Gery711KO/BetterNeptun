package hu.kocsisgeri.betterneptun.ui.screen.messages.detail

import hu.kocsisgeri.betterneptun.domain.usecase.messages.GetMessageDetailUseCase
import hu.kocsisgeri.betterneptun.ui.core.ComposeViewModel

class MessageDetailViewModel(
    messageId: String,
    getMessageDetailUseCase: GetMessageDetailUseCase,
) : ComposeViewModel() {

    val message = getMessageDetailUseCase(messageId).stateWhileSubscribed(null)
}