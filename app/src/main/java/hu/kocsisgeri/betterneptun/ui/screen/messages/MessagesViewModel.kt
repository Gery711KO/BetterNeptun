package hu.kocsisgeri.betterneptun.ui.screen.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import kotlinx.coroutines.launch

class MessagesViewModel(
    private val neptunRepository: NeptunRepository
) : ViewModel() {

    private val itemFlow = neptunRepository.messages
    val listItems = itemFlow.asLiveData()

    fun getCurrentPosition() = (neptunRepository.currentMessagePage - 1) * 10 + 4

    fun readMessage(messageId : Int) {
        viewModelScope.launch {
            neptunRepository.readMessage(messageId)
        }
    }
}