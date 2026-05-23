package hu.kocsisgeri.betterneptun.domain.model.neptun

data class MessagesPager(
    val messages: List<Message> = emptyList(),
    val isLoadingNextMessages: Boolean = true,
    val isEndReached: Boolean = false,
    val error: String? = null
)