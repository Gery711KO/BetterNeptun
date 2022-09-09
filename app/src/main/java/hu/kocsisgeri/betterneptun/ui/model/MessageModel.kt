package hu.kocsisgeri.betterneptun.ui.model

import hu.kocsisgeri.betterneptun.ui.adapter.ListItem
import java.io.Serializable
import java.util.*

data class MessageModel(
    val id: Int,
    val detail: String,
    val name: String,
    val subject: String,
    val date: Date,
    val isNew : Boolean
): ListItem, Serializable