package hu.kocsisgeri.betterneptun.domain.usecase.messages

import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.domain.usecase.UseCase

class LoadMoreMessagesUseCase(private val neptunRepository: NeptunRepository): UseCase() {

    suspend operator fun invoke() = withLock {
        neptunRepository.fetchMessages(isRefresh = false)
    }
}
