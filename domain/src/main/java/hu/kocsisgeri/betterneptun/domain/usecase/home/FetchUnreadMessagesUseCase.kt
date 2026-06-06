package hu.kocsisgeri.betterneptun.domain.usecase.home

import hu.kocsisgeri.betterneptun.domain.repository.neptun.MessagesRepository
import hu.kocsisgeri.betterneptun.domain.usecase.UseCase
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Factory

@Factory
class FetchUnreadMessagesUseCase(private val messagesRepository: MessagesRepository): UseCase() {

    operator fun invoke() = lockedFlow {
        messagesRepository.fetchUnreadMessages()

        emit(messagesRepository.unreadMessagesCount.first())
    }
}
