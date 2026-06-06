package hu.kocsisgeri.betterneptun.common.utils

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern


object DateUtils {

    @OptIn(FormatStringsInDatetimeFormats::class)
    private val format = LocalDateTime.Format {
        byUnicodePattern("uuuu-MM-dd HH:mm")
    }

    fun formatDate(date: LocalDateTime): String = date.format(format)
}
