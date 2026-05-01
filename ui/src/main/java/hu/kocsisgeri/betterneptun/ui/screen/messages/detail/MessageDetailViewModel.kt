package hu.kocsisgeri.betterneptun.ui.screen.messages.detail

import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.common.launchReportingErrors
import hu.kocsisgeri.betterneptun.domain.model.MessageDetail
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.ui.base.ComposeViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class MessageDetailViewModel(
    private val messageId: String,
    private val neptunRepository: NeptunRepository,
) : ComposeViewModel() {

    private val _isError = MutableStateFlow<String?>(null)
    val isError = _isError.stateWhileSubscribed()
    
    private val _messageDetail = MutableStateFlow<MessageDetail?>(null)
    val messageDetail = _messageDetail.stateWhileSubscribed()
    
    fun refresh() {
        _isError.value = null
        viewModelScope.launchReportingErrors {
            try {
                val messageDetail = neptunRepository.getMessageDetail(messageId)
                _messageDetail.value = messageDetail

                neptunRepository.readMessage(messageId, messageDetail)
            } catch (exception: Exception) {
                _isError.value = "Hiba történt az üzenet betöltése közben.\n Kérlek próbáld újra."
            }
        }
    }

    init {
        refresh()
    }
}