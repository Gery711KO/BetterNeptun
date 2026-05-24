package hu.kocsisgeri.betterneptun.domain.usecase.messages

import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.domain.usecase.UseCase

class RefreshMessagesUseCase(private val neptunRepository: NeptunRepository): UseCase() {

    suspend operator fun invoke() = withLock {
        if (neptunRepository.messages.value.messages.isEmpty()) {
            neptunRepository.fetchMessages(isRefresh = true)
        } else {
            neptunRepository.checkForMessageUpdates()
        }
    }
}
