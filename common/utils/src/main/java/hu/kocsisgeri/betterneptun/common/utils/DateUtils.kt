package hu.kocsisgeri.betterneptun.common.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateUtils {

    private val format = DateTimeFormatter.ofPattern("yyyy. MM. dd. HH:mm")

    fun formatDate(date: LocalDateTime): String = date.format(format)
}
