package hu.kocsisgeri.betterneptun.domain.model

data class Message(
    val id: String,
    val name: String,
    val subject: String,
    val date: String,
    val isNew : Boolean,
    val detail: String = ""
)
