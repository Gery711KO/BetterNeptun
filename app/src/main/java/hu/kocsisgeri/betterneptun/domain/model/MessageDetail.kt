package hu.kocsisgeri.betterneptun.domain.model

import java.time.LocalDateTime

data class MessageDetail(
    val sender: String,
    val subject: String,
    val date: LocalDateTime,
    val posts: List<Post>,
) {

    data class Post(
        val id: String,
        val htmlText: String,
    )
}
