package hu.kocsisgeri.betterneptun.ui.timetable.model

import android.graphics.Color
import android.graphics.RectF
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.text.style.TypefaceSpan
import com.alamkanak.weekview.WeekViewEntity
import com.alamkanak.weekview.jsr310.WeekViewPagingAdapterJsr310
import com.alamkanak.weekview.jsr310.setEndTime
import com.alamkanak.weekview.jsr310.setStartTime
import hu.kocsisgeri.betterneptun.data.repository.neptun.NeptunRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import kotlin.coroutines.CoroutineContext

sealed class CalendarEntity {

    data class Event(
        val id: Long,
        val title: CharSequence,
        val courseCode: String,
        val subjectCode : String,
        val teacher: String,
        val startTime: LocalDateTime,
        val endTime: LocalDateTime,
        val location: CharSequence,
        val color: Int,
        val isAllDay: Boolean,
        val isCanceled: Boolean
    ) : CalendarEntity(), Serializable

    data class BlockedTimeSlot(
        val id: Long,
        val startTime: LocalDateTime,
        val endTime: LocalDateTime
    ) : CalendarEntity()
}

fun CalendarEntity.toWeekViewEntity(): WeekViewEntity {
    return when (this) {
        is CalendarEntity.Event -> toWeekViewEntity()
        is CalendarEntity.BlockedTimeSlot -> toWeekViewEntity()
    }
}

fun CalendarEntity.Event.toWeekViewEntity(): WeekViewEntity {
    val backgroundColor = if (!isCanceled) color else Color.WHITE
    val textColor = if (!isCanceled) Color.WHITE else color
    //val borderWidthResId = if (!isCanceled) R.dimen.no_border_width else R.dimen.border_width

    val style = WeekViewEntity.Style.Builder()
        .setTextColor(textColor)
        .setBackgroundColor(backgroundColor)
        //.setBorderWidthResource(borderWidthResId)
        .setBorderColor(color)
        .build()

    val title = SpannableStringBuilder(title).apply {
        val titleSpan = TypefaceSpan("armata")
        setSpan(titleSpan, 0, title.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        if (isCanceled) {
            setSpan(StrikethroughSpan(), 0, title.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    val subtitle = SpannableStringBuilder("\n$location").apply {
        if (isCanceled) {
            setSpan(StrikethroughSpan(), 0, location.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    return WeekViewEntity.Event.Builder(this)
        .setId(id)
        .setTitle(title)
        .setStartTime(startTime)
        .setEndTime(endTime)
        .setAllDay(isAllDay)
        .setStyle(style)
        .build()
}

fun CalendarEntity.BlockedTimeSlot.toWeekViewEntity(): WeekViewEntity {
    val style = WeekViewEntity.Style.Builder()
        //.setBackgroundColorResource(R.color.gray_alpha10)
        .setCornerRadius(0)
        .build()

    return WeekViewEntity.BlockedTime.Builder()
        .setId(id)
        .setStartTime(startTime)
        .setEndTime(endTime)
        .setStyle(style)
        .build()
}

class FragmentWeekViewAdapter(
    private val loadMoreHandler: () -> Unit,
    val clickHandler: MutableSharedFlow<CalendarEntity.Event>
) : WeekViewPagingAdapterJsr310<CalendarEntity>(), KoinComponent, CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.IO
    private val neptunRepository : NeptunRepository by inject()

    override fun onCreateEntity(item: CalendarEntity): WeekViewEntity = item.toWeekViewEntity()

    override fun onLoadMore(
        startDate: LocalDate,
        endDate: LocalDate
    ) = loadMoreHandler()

    override fun onEventClick(data: CalendarEntity, bounds: RectF) {
        super.onEventClick(data, bounds)
        clickHandler.tryEmit(data as CalendarEntity.Event)
    }
}

fun yearMonthsBetween(startDate: LocalDate, endDate: LocalDate): List<YearMonth> {
    val yearMonths = mutableListOf<YearMonth>()
    val maxYearMonth = endDate.yearMonth
    var currentYearMonth = startDate.yearMonth

    while (currentYearMonth <= maxYearMonth) {
        yearMonths += currentYearMonth
        currentYearMonth = currentYearMonth.plusMonths(1)
    }

    return yearMonths
}

private val LocalDate.yearMonth: YearMonth
    get() = YearMonth.of(year, month)
