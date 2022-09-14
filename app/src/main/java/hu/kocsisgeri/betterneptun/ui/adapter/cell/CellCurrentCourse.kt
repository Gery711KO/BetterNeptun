package hu.kocsisgeri.betterneptun.ui.adapter.cell

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import hu.kocsisgeri.betterneptun.databinding.CellCurrentCourseBinding
import hu.kocsisgeri.betterneptun.ui.adapter.ListItem
import hu.kocsisgeri.betterneptun.ui.timetable.model.CalendarEntity
import hu.kocsisgeri.betterneptun.utils.getTimeLeft
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

fun cellCurrentCourseDelegate() =
    adapterDelegateViewBinding<CalendarEntity.Event, ListItem, CellCurrentCourseBinding>(
        viewBinding = { layoutInflater, parent ->
            CellCurrentCourseBinding.inflate(layoutInflater, parent, false)
        },
        block = {
            bind {
                binding.lineProgress.max = item.getTime().ceil()
                binding.lineProgress.progress = item.getPercent()
                binding.currentCourseLabel.text = "Éppen tart"
                binding.lineProgress.setIndicatorColor(item.color)
                binding.currentCourseLocation.text = item.location.trim()
                binding.currentCourseSubject.text = item.title.trimStart()
                binding.currentCourseTimeLeft.text = item.endTime.getTimeLeft()
            }
        }
    )


private fun CalendarEntity.Event.getRemainingTime() : Float {
    val diff = endTime.toEpochSecond(ZoneOffset.UTC) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(diff * 1000)
    return seconds / 60f
}

private fun CalendarEntity.Event.getTime() : Float {
    val diff = endTime.toEpochSecond(ZoneOffset.UTC) - startTime.toEpochSecond(ZoneOffset.UTC)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(diff * 1000)
    return seconds / 60f
}

private fun CalendarEntity.Event.getPercent(): Int {
    return (getTime() - ((getRemainingTime() / getTime()) * 100)).roundToInt()
}

private fun Float.ceil() : Int {
    return kotlin.math.ceil(this).roundToInt()
}