package hu.kocsisgeri.betterneptun.domain.usecase.home

import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.domain.usecase.UseCase
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Factory

@Factory
class FetchUnreadMessagesUseCase(private val neptunRepository: NeptunRepository): UseCase() {

    operator fun invoke() = lockedFlow {
        neptunRepository.fetchUnreadMessages()

        emit(neptunRepository.unreadMessagesCount.first())
    }
}
