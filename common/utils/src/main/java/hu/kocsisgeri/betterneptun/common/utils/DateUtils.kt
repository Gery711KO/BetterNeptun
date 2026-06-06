package hu.kocsisgeri.betterneptun.common.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.format.char


internal object DateUtils {

    @OptIn(FormatStringsInDatetimeFormats::class)
    internal val apiResultFormatter = LocalDateTime.Format {
        byUnicodePattern("uuuu-MM-dd HH:mm")
    }

    @OptIn(FormatStringsInDatetimeFormats::class)
    internal val datePickerFormatter = LocalDateTime.Format {
        byUnicodePattern("uuuu. MM. dd.")
    }

    internal val monthDayFormat = LocalDate.Format {
        monthNumber()
        char('/')
        day()
    }

    internal val timePickerFormatter = LocalTime.Format {
        hour()
        char(':')
        minute()
    }

    internal fun monthFormatter(names: MonthNames) = LocalDate.Format {
        monthName(names)
    }
}


/**
 * Format the [LocalDateTime].
 * @return The formatted string. Example: '2000-01-01 11:30'
 */
fun LocalDateTime.formatApiDate(): String = DateUtils.apiResultFormatter.format(this)

/**
 * Format the [LocalDateTime].
 * @return The formatted string. Example: '2000. 03. 01'
 */
fun LocalDateTime.formatDatePickerDate(): String = DateUtils.datePickerFormatter.format(this)

/**
 * Format the [LocalDate].
 * @return The formatted string. Example: '2000. 03. 01'
 */
fun LocalDate.formatMonth(names: MonthNames): String = DateUtils.monthFormatter(names).format(this)

/**
 * Format the [LocalTime].
 * @return The formatted string. Example: '11:30'
 */
fun LocalTime.formatTimePickerDate(): String = DateUtils.timePickerFormatter.format(this)

