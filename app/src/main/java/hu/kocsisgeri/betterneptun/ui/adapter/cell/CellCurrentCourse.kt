package hu.kocsisgeri.betterneptun.ui.adapter.cell

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import hu.kocsisgeri.betterneptun.databinding.CellCurrentCourseBinding
import hu.kocsisgeri.betterneptun.ui.adapter.ListItem
import hu.kocsisgeri.betterneptun.ui.timetable.model.CalendarEntity
import hu.kocsisgeri.betterneptun.utils.getPercent
import hu.kocsisgeri.betterneptun.utils.getTimeLeft
import timber.log.Timber
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
                binding.lineProgress.max = 100
                binding.lineProgress.progress = item.getPercent()
                binding.currentCourseLabel.text = "Éppen tart"
                binding.lineProgress.setIndicatorColor(item.color)
                binding.currentCourseLocation.text = item.location.trim()
                binding.currentCourseSubject.text = item.title.trimStart()
                binding.currentCourseTimeLeft.text = item.endTime.getTimeLeft()
            }
        }
    )