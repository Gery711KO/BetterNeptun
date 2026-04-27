package hu.kocsisgeri.betterneptun.ui.screen.messages.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.domain.model.MessageDetail
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.utils.launchReportingErrors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MessageDetailViewModel(
    private val messageId: String,
    private val neptunRepository: NeptunRepository,
) : ViewModel() {

    private val _isError = MutableStateFlow<String?>(null)
    val isError = _isError.asStateFlow()
    
    private val _messageDetail = MutableStateFlow<MessageDetail?>(null)
    val messageDetail = _messageDetail.asStateFlow()
    
    fun refresh() {
        _isError.value = null
        viewModelScope.launchReportingErrors {
            try {
                _messageDetail.value = neptunRepository.getMessageDetail(messageId)
            } catch (exception: Exception) {
                _isError.value = "Hiba történt az üzenet betöltése közben.\n Kérlek próbáld újra."
            }
        }
    }

    init {
        refresh()
    }
}