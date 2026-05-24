package hu.kocsisgeri.betterneptun.domain.usecase.messages

import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import kotlinx.coroutines.CoroutineScope

class RefreshMessagesUseCase(private val neptunRepository: NeptunRepository) {

    operator fun invoke(
        onLaunch: (suspend CoroutineScope.() -> Unit) -> Unit
    ) {
        if (neptunRepository.messages.value.messages.isEmpty()) {
            onLaunch {
                neptunRepository.fetchMessages(isRefresh = true)
            }
        } else {
            onLaunch {
                neptunRepository.checkForMessageUpdates()
            }
        }
    }
}
