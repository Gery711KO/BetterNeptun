package hu.kocsisgeri.betterneptun.domain.usecase.messages

import hu.kocsisgeri.betterneptun.domain.repository.neptun.MessagesRepository
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

@Factory
class GetMessageDetailUseCase(private val messagesRepository: MessagesRepository) {

    operator fun invoke(messageId: String) = messagesRepository.messages.map { pager ->
        pager.messages.find { it.id == messageId }
    }
}
