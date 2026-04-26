package hu.kocsisgeri.betterneptun.domain.model

data class MessageDetail(
    val sender: String,
    val subject: String,
    val date: String,
    val posts: List<Post>,
) {

    data class Post(
        val id: String,
        val htmlText: String,
    )
}
