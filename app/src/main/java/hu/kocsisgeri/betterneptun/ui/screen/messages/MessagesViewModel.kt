package hu.kocsisgeri.betterneptun.ui.screen.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.domain.model.MessageDetail
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MessagesViewModel(
    private val neptunRepository: NeptunRepository
) : ViewModel() {

    private val itemFlow = neptunRepository.messages
    val listItems = itemFlow.asLiveData()

    private val _messageDetail = MutableStateFlow<MessageDetail?>(null)
    val messageDetail = _messageDetail.asLiveData()

    init {
        viewModelScope.launch {
            neptunRepository.fetchMessages()
        }
    }

    fun readMessage(messageId : String) {
        viewModelScope.launch {
            _messageDetail.value = neptunRepository.getMessageDetail(messageId)
        }
    }

    fun clearMessageDetail() {
        _messageDetail.value = null
    }
}