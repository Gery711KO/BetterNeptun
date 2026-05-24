package hu.kocsisgeri.betterneptun.domain.usecase.messages

import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository

class LoadMoreMessagesUseCase(private val neptunRepository: NeptunRepository) {

    suspend operator fun invoke(isLoading: Boolean) {
        if (isLoading) return

        neptunRepository.fetchMessages(isRefresh = false)
    }
}
