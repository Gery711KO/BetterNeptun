package hu.kocsisgeri.betterneptun.domain.usecase.messages

import hu.kocsisgeri.betterneptun.domain.repository.neptun.MessagesRepository
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

@Factory
class GetMessagesPagerUseCase(private val messagesRepository: MessagesRepository) {

    operator fun invoke() = messagesRepository.messages.map { pager ->
        pager.copy(messages = pager.messages.distinctBy { it.id })
    }
}
