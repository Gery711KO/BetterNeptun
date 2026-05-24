package hu.kocsisgeri.betterneptun.domain.usecase.messages

import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

@Factory
class GetMessagesPagerUseCase(private val neptunRepository: NeptunRepository) {

    operator fun invoke() = neptunRepository.messages.map { pager ->
        pager.copy(messages = pager.messages.distinctBy { it.id })
    }
}
