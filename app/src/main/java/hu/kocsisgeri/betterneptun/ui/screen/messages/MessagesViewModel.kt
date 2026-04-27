package hu.kocsisgeri.betterneptun.ui.screen.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.domain.model.MessageDetail
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.utils.launchReportingErrors
import kotlinx.coroutines.flow.MutableStateFlow

class MessagesViewModel(
    private val neptunRepository: NeptunRepository
) : ViewModel() {

    private val itemFlow = neptunRepository.messages
    val listItems = itemFlow.asLiveData()

    private val _messageDetail = MutableStateFlow<MessageDetail?>(null)
    val messageDetail = _messageDetail.asLiveData()

    init {
        viewModelScope.launchReportingErrors {
            neptunRepository.fetchMessages()
        }
    }

    fun readMessage(messageId : String) {
        viewModelScope.launchReportingErrors {
            _messageDetail.value = neptunRepository.getMessageDetail(messageId)
        }
    }
}