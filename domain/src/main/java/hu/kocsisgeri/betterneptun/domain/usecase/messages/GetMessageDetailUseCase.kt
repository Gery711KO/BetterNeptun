package hu.kocsisgeri.betterneptun.domain.usecase.messages

import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

@Factory
class GetMessageDetailUseCase(private val neptunRepository: NeptunRepository) {

    operator fun invoke(messageId: String) = neptunRepository.messages.map { pager ->
        pager.messages.find { it.id == messageId }
    }
}
