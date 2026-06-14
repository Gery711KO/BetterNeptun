package hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import hu.kocsisgeri.betterneptun.common.utils.isAfter
import hu.kocsisgeri.betterneptun.common.utils.isBefore
import hu.kocsisgeri.betterneptun.common.utils.minutesUntil
import hu.kocsisgeri.betterneptun.common.utils.plus
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.style.WeekViewStyle
import kotlinx.coroutines.launch
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
    onZoom: (Float) -> Unit,
    onSelectionChanged: (date: LocalDateTime?) -> Unit,
) {
    val addIcon = rememberVectorPainter(Icons.Rounded.Add)
    val density = LocalDensity.current

    var selectedDay by remember { mutableStateOf<LocalDateTime?>(null) }
    val animatedColumnIndex = remember { Animatable(0f) }
    val animatedHourIndex = remember { Animatable(0f) }
    val rowHeightPx = with(density) { rowHeightDp.toPx() }

    LaunchedEffect(selectedDay) {
        selectedDay?.let { selectedDay ->
            val targetColumn = days.indexOf(selectedDay.date).toFloat()
            val targetHour = (selectedDay.time.hour - gridStartTime.hour).toFloat()

            if (targetColumn >= 0) {
                launch {
                    animatedColumnIndex.animateTo(
                        targetColumn,
                        spring(stiffness = 400f, dampingRatio = 0.75f)
                    )
                }
                launch {
                    animatedHourIndex.animateTo(
                        targetHour,
                        spring(stiffness = 400f, dampingRatio = 0.75f)
                    )
                }
            }
        }
    }

    Canvas(
        modifier = modifier
            .pointerInput(columnCount, days, rowHeightPx, gridStartTime) {
                val touchSlop = viewConfiguration.touchSlop

                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    var isMultiTouch = false
                    var hasMovedTooMuch = false
                    val startPosition = down.position

                    do {
                        val event = awaitPointerEvent()

                        if (event.changes.size >= 2) {
                            isMultiTouch = true
                            val zoom = event.calculateZoom()
                            if (zoom != 1f) {
                                onZoom(zoom)
                                event.changes.forEach { it.consume() }
                            }
                        } else if (event.changes.size == 1) {
                            val currentPosition = event.changes.first().position
                            val distance = (currentPosition - startPosition).getDistance()

                            if (distance > touchSlop) {
                                hasMovedTooMuch = true
                            }
                        }
                    } while (event.changes.any { it.pressed })

                    if (!isMultiTouch && !hasMovedTooMuch && !down.isConsumed) {
                        val offset = down.position
                        val columnWidthPx =
                            if (columnCount > 0) size.width / columnCount else size.width
                        val clickedColumnIndex = (offset.x / columnWidthPx).toInt()

                        if (clickedColumnIndex in days.indices) {
                            val clickedDay = days[clickedColumnIndex]
                            val clickedHoursFromStart = (offset.y / rowHeightPx).toInt()
                            val clickedTime = gridStartTime.plus(clickedHoursFromStart.toLong().hours)

                            val newSelection = clickedDay.atTime(clickedTime)
                            selectedDay = newSelection
                            onSelectionChanged(newSelection)
                        } else {
                            selectedDay = null
                            onSelectionChanged(null)
                        }
                        down.consume()
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
                val hoursFromStart = selectedDay.time.hour - gridStartTime.hour

                if (hoursFromStart >= 0 && hoursFromStart < totalHours) {

                    val boxLeft = animatedColumnIndex.value * columnWidthPx
                    val boxTop = animatedHourIndex.value * rowHeightPx

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
                                size = Size(
                                    iconSize.coerceAtLeast(1f),
                                    iconSize.coerceAtLeast(1f)
                                ),
                                colorFilter = ColorFilter.tint(style.colors.nowIndicator)
                            )
                        }
                    }

                    drawRect(
                        color = selectionColor,
                        topLeft = Offset(boxLeft, boxTop),
                        size = boxSize,
                        style = Stroke(width = 4f)
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
