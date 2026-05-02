package hu.kocsisgeri.betterneptun.data.mapper

import hu.kocsisgeri.betterneptun.data.model.MessageDetailsDto
import hu.kocsisgeri.betterneptun.data.model.ReceivedMessageDto
import hu.kocsisgeri.betterneptun.domain.model.Avatar
import hu.kocsisgeri.betterneptun.domain.model.Message
import hu.kocsisgeri.betterneptun.domain.model.MessageDetail

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