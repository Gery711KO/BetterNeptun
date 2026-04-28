package hu.kocsisgeri.betterneptun.data.mapper

import hu.kocsisgeri.betterneptun.data.dao.MessageEntity
import hu.kocsisgeri.betterneptun.data.model.MessageDetailsDto
import hu.kocsisgeri.betterneptun.data.model.ReceivedMessageDto
import hu.kocsisgeri.betterneptun.domain.model.Message
import hu.kocsisgeri.betterneptun.domain.model.MessageDetail

fun MessageEntity.toMessageDomain() = Message(
    id = id,
    name = senderName,
    subject = subject,
    date = date,
    isNew = isNew,
)

fun ReceivedMessageDto.toMessageEntity() = MessageEntity(
    id = messageId,
    senderName = if (isSystemMessage) "Rendszerüzenet" else senderName,
    subject = subject,
    date = lastPostDate,
    isNew = unreadedPostCount > 0,
)

fun MessageDetailsDto.toMessageDomain() = MessageDetail(
    sender = recipients.find {
        posts.first().senderUserId == it.userId
    }?.printName ?: "Rendszerüzenet",
    subject = messageData.subject,
    date = posts.last().sendDate,
    posts = posts.map {
        MessageDetail.Post(
            id = it.postId,
            htmlText = it.htmlText
        )
    },
)