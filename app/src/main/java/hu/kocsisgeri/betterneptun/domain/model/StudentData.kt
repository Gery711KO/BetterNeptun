package hu.kocsisgeri.betterneptun.domain.model

import java.io.Serializable

data class StudentData(
    val name: String?,
    val neptun: String?,
    val unreadMessages: String?,
    val maxPage: Int?
): Serializable