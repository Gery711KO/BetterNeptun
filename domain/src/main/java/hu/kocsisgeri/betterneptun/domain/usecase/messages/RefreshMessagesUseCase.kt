package hu.kocsisgeri.betterneptun.domain.usecase.messages

import hu.kocsisgeri.betterneptun.domain.repository.neptun.MessagesRepository
import hu.kocsisgeri.betterneptun.domain.usecase.UseCase
import org.koin.core.annotation.Factory

@Factory
class RefreshMessagesUseCase(private val messagesRepository: MessagesRepository): UseCase() {

    suspend operator fun invoke() = withLock {
        if (messagesRepository.messages.value.messages.isEmpty()) {
            messagesRepository.fetchMessages(isRefresh = true)
        } else {
            messagesRepository.checkForMessageUpdates()
        }
    }
}
