package hu.kocsisgeri.betterneptun.domain.api.dto

import android.annotation.SuppressLint
import hu.kocsisgeri.betterneptun.ui.model.MessageModel
import java.util.*

data class MessageResponseDto(
    val CurrentPage: Int?,
    val MessagesList: List<MessageDto>?
)

data class MessageDto(
    val Detail: String,
    val Id: Int,
    val Name: String,
    val Subject: String,
    val SendDate: String,
    val IsNew : Boolean
) {
    fun mapToModel() : MessageModel {
        return MessageModel(
            id = Id,
            isNew = IsNew,
            name = Name,
            subject = Subject,
            detail = Detail,
            date = SendDate.toDate()
        )
    }

    @SuppressLint("SimpleDateFormat")
    fun String.toDate(): Date {
        return Date(this.split("(")[1].split(")")[0].toLong() - 7200000)
    }
}

