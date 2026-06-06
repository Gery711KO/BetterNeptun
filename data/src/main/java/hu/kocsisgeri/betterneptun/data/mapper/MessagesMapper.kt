package hu.kocsisgeri.betterneptun.data.mapper

import hu.kocsisgeri.betterneptun.core.network.model.neptun.MessageDetailsDto
import hu.kocsisgeri.betterneptun.core.network.model.neptun.ReceivedMessageDto
import hu.kocsisgeri.betterneptun.domain.model.neptun.Avatar
import hu.kocsisgeri.betterneptun.domain.model.neptun.Message
import hu.kocsisgeri.betterneptun.domain.model.neptun.MessageDetail

fun ReceivedMessageDto.toMessageDomain() = Message(
    id = messageId,
    name = if (isSystemMessage) "Rendszerüzenet" else senderName,
    subject = subject,
    date = lastPostDate,
    isNew = unreadedPostCount > 0,
    senderAvatar = Avatar.SystemAvatar
)

fun MessageDetailsDto.toMessageDomain() = MessageDetail(
    sender = recipients.find {
        posts.first().senderUserId == it.userId
    }?.printName ?: "Rendszerüzenet",
    subject = messageData.subject,
    date = posts.last().sendDate,
    hasUnreadPost = posts.any { it.isRead.not() },
    posts = posts.map {
        MessageDetail.Post(
            id = it.postId,
            htmlText = it.htmlText
        )
    },
)
