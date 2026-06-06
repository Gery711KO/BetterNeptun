package hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import hu.kocsisgeri.betterneptun.common.utils.isAfter
import hu.kocsisgeri.betterneptun.common.utils.isBefore
import hu.kocsisgeri.betterneptun.common.utils.minutesUntil
import hu.kocsisgeri.betterneptun.common.utils.plus
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.style.WeekViewStyle
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atTime
import kotlin.math.min
import kotlin.time.Duration.Companion.hours

@Composable
internal fun GridCanvas(
    modifier: Modifier = Modifier,
    columnCount: Int,
    rowHeightDp: Dp,
    totalHours: Float,
    days: List<LocalDate>,
    today: LocalDate,
    showNowIndicator: Boolean,
    highlightCurrentDay: Boolean,
    currentTimeLineOnlyToday: Boolean,
    now: LocalTime,
    gridStartTime: LocalTime,
    effectiveEndTime: LocalTime,
    style: WeekViewStyle,
    onSelectionChanged: (date: LocalDateTime?) -> Unit
) {
    val addIcon = rememberVectorPainter(Icons.Rounded.Add)

    var selectedDay by remember { mutableStateOf<LocalDateTime?>(null) }

    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val columnWidthPx =
                        if (columnCount > 0) size.width / columnCount else size.width
                    val rowHeightPx = rowHeightDp.toPx()

                    val clickedColumnIndex = (offset.x / columnWidthPx).toInt()

                    if (clickedColumnIndex in days.indices) {
                        val clickedDay = days[clickedColumnIndex]
                        val clickedHoursFromStart = (offset.y / rowHeightPx).toInt()

                        val clickedTime = gridStartTime.plus(clickedHoursFromStart.toLong().hours)

                        selectedDay = clickedDay.atTime(clickedTime)
                        onSelectionChanged(selectedDay)
                    } else {
                        selectedDay = null
                        onSelectionChanged(null)
                    }
                }
            }
    ) {
        val columnWidthPx = if (columnCount > 0) size.width / columnCount else size.width
        val rowHeightPx = rowHeightDp.toPx()

        for (i in 0..columnCount) {
            val x = i * columnWidthPx
            drawLine(
                color = style.colors.gridLineColor,
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 2f,
            )
        }

        val hourLineCount = kotlin.math.ceil(totalHours).toInt()
        for (i in 0..hourLineCount) {
            val y = i * rowHeightPx
            drawLine(
                color = style.colors.gridLineColor,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 2f,
            )
        }

        if (highlightCurrentDay && days.contains(today)) {
            val todayColumnIndex = days.indexOf(today)
            val left = todayColumnIndex * columnWidthPx
            drawRect(
                color = style.colors.todayHighlight,
                topLeft = Offset(left, 0f),
                size = Size(columnWidthPx, size.height),
            )
        }

        selectedDay?.let { selectedDay ->
            if (days.contains(selectedDay.date)) {
                val selectionColumnIndex = days.indexOf(selectedDay.date)

                val hoursFromStart = selectedDay.time.hour - gridStartTime.hour

                if (hoursFromStart >= 0 && hoursFromStart < totalHours) {
                    val boxLeft = selectionColumnIndex * columnWidthPx
                    val boxTop = hoursFromStart * rowHeightPx

                    val boxHeight = rowHeightPx
                    val boxSize = Size(columnWidthPx, boxHeight)

                    val selectionColor = style.colors.nowIndicator

                    drawRect(
                        color = selectionColor.copy(alpha = 0.15f),
                        topLeft = Offset(boxLeft, boxTop),
                        size = boxSize
                    )

                    val iconSize = min(boxSize.width, boxSize.height) / 2f

                    withTransform(
                        transformBlock = {
                            translate(
                                left = boxLeft + (boxSize.width - iconSize) / 2f,
                                top = boxTop + (boxSize.height - iconSize) / 2f
                            )
                        }
                    ) {
                        with(addIcon) {
                            draw(
                                size = Size(iconSize, iconSize),
                                colorFilter = ColorFilter.tint(style.colors.nowIndicator)
                            )
                        }
                    }


                    drawRect(
                        color = selectionColor,
                        topLeft = Offset(boxLeft, boxTop),
                        size = boxSize,
                        style = Stroke(width = 4f) // 4px vastag keret
                    )
                }
            }
        }

        if (showNowIndicator && now.isAfter(gridStartTime) && now.isBefore(effectiveEndTime)) {
            val nowPositionMinutes = gridStartTime.minutesUntil(now)
            val nowY = (nowPositionMinutes / 60f) * rowHeightPx
            if (nowY >= 0 && nowY <= size.height) {
                val dotRadius = 8f
                if (currentTimeLineOnlyToday) {
                    if (days.contains(today)) {
                        val todayColumnIndex = days.indexOf(today)
                        val left = todayColumnIndex * columnWidthPx
                        val right = left + columnWidthPx
                        drawLine(
                            color = style.colors.nowIndicator,
                            start = Offset(left, nowY),
                            end = Offset(right, nowY),
                            strokeWidth = 4f,
                        )
                        drawCircle(
                            color = style.colors.nowIndicator,
                            radius = dotRadius,
                            center = Offset(left, nowY),
                        )
                    }
                } else {
                    drawLine(
                        color = style.colors.nowIndicator,
                        start = Offset(0f, nowY),
                        end = Offset(size.width, nowY),
                        strokeWidth = 4f,
                    )
                    drawCircle(
                        color = style.colors.nowIndicator,
                        radius = dotRadius,
                        center = Offset(0f, nowY),
                    )
                }
            }
        }
    }
}
