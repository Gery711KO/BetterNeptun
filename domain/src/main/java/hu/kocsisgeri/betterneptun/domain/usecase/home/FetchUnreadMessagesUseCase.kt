package hu.kocsisgeri.betterneptun.domain.usecase.home

import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first

class FetchUnreadMessagesUseCase(private val neptunRepository: NeptunRepository) {

    operator fun invoke() = callbackFlow {
        neptunRepository.fetchUnreadMessages()

        send(neptunRepository.unreadMessagesCount.first())

        awaitClose()
    }
}
