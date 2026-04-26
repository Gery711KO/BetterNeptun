package hu.kocsisgeri.betterneptun.ui.model

import hu.kocsisgeri.betterneptun.ui.adapter.ListItem
import java.io.Serializable
import java.util.*

data class MessageModel(
    val id: String,
    val detail: String,
    val name: String,
    val subject: String,
    val date: Date,
    val isNew : Boolean
): ListItem, Serializable {
    override fun getAdapterItemId(): String {
        return id
    }

    override fun getAdapterItemHash(): Int {
        return id.hashCode() + isNew.hashCode()
    }
}