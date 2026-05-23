package hu.kocsisgeri.betterneptun.domain.model.neptun

import java.time.LocalDateTime

data class MessageDetail(
    val sender: String,
    val subject: String,
    val date: LocalDateTime,
    val hasUnreadPost: Boolean,
    val posts: List<Post>,
) {

    data class Post(
        val id: String,
        val htmlText: String,
    )
}
