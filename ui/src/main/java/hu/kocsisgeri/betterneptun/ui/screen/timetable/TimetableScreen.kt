package hu.kocsisgeri.betterneptun.ui.screen.timetable

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.tobiasschuerg.weekview.compose.WeekViewActions
import de.tobiasschuerg.weekview.data.EventConfig
import de.tobiasschuerg.weekview.data.LocalDateRange
import de.tobiasschuerg.weekview.data.WeekData
import de.tobiasschuerg.weekview.data.WeekViewConfig
import hu.kocsisgeri.betterneptun.domain.model.CalendarItem
import hu.kocsisgeri.betterneptun.ui.R
import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
import hu.kocsisgeri.betterneptun.ui.screen.timetable.dialog.AddEventDialog
import hu.kocsisgeri.betterneptun.ui.screen.timetable.dialog.CourseDetailDialog
import hu.kocsisgeri.betterneptun.ui.screen.timetable.model.toComposeEvent
import hu.kocsisgeri.betterneptun.ui.theme.BetterNeptunTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters

@Composable
fun TimetableScreen(
    viewModel: TimetableViewModel = koinViewModel(),
    navigator: Navigator = koinInject()
) {
    val events by viewModel.timetableEvents.collectAsStateWithLifecycle()
    val times by viewModel.times.collectAsStateWithLifecycle()
    val viewMode by viewModel.viewMode.collectAsStateWithLifecycle()
    val dateRange by viewModel.dateRange.collectAsStateWithLifecycle()
    val currentSelectedEvent by viewModel.selectedEvent.collectAsStateWithLifecycle()

    TimetableContent(
        events = events,
        times = times,
        viewMode = viewMode,
        dateRange = dateRange,
        currentSelectedEvent = currentSelectedEvent,
        onNavigateBack = navigator::navigateBack,
        onViewModeChange = viewModel::setViewMode,
        onNext = viewModel::next,
        onPrevious = viewModel::previous,
        onEventClick = viewModel::selectEvent,
        onDismissDetail = viewModel::clearSelectedEvent,
        onDeleteEvent = viewModel::deleteEvent,
        onAddEvent = viewModel::addEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableContent(
    events: List<CalendarItem>,
    times: String,
    viewMode: ViewMode,
    dateRange: LocalDateRange,
    currentSelectedEvent: CalendarItem?,
    onNavigateBack: () -> Unit,
    onViewModeChange: (ViewMode) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onEventClick: (Long) -> Unit,
    onDismissDetail: () -> Unit,
    onDeleteEvent: (Long) -> Unit,
    onAddEvent: (CalendarItem.LocalEvent) -> Unit,
) {
    var showAddEventDialog by remember { mutableStateOf(false) }

    val weekData by remember(events, times, dateRange) {
        derivedStateOf {
            val splitTimes = times.split(":")
            val minHour = splitTimes.getOrNull(0)?.toIntOrNull() ?: 7
            val maxHour = splitTimes.getOrNull(1)?.toIntOrNull() ?: 23

            WeekData(
                dateRange = dateRange,
                start = LocalTime.of(minHour, 0),
                end = LocalTime.of(maxHour, 0)
            ).apply {
                events.forEach {
                    add(it.toComposeEvent())
                }
            }
        }
    }

    CourseDetailDialog(
        selectedEvent = currentSelectedEvent,
        currentColor = currentSelectedEvent?.color ?: 0,
        onDismissRequest = onDismissDetail,
        onEditEvent = { showAddEventDialog = true },
        onDeleteLocalEvent = onDeleteEvent
    )

    if (showAddEventDialog) {
        AddEventDialog(
            event = currentSelectedEvent as? CalendarItem.LocalEvent,
            onDismissRequest = {
                showAddEventDialog = false
                onDismissDetail()
            },
            onAddEvent = onAddEvent
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Órarend",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Vissza"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onPrevious) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Előző"
                        )
                    }
                    IconButton(onClick = onNext) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Következő"
                        )
                    }
                    IconButton(onClick = {
                        val newMode = when (viewMode) {
                            ViewMode.WEEK -> ViewMode.DAY
                            ViewMode.DAY -> ViewMode.WEEK
                        }
                        onViewModeChange(newMode)
                    }) {
                        Icon(
                            painter = painterResource(id = viewMode.icon),
                            contentDescription = "Nézet váltás",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddEventDialog = true }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_event),
                    contentDescription = "Esemény hozzáadása"
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .clip(MaterialTheme.shapes.large)
        ) {
            TimeTableView(
                weekData = weekData,
                modifier = Modifier.fillMaxSize(),
                weekViewConfig = WeekViewConfig(
                    showCurrentTimeIndicator = true,
                    highlightCurrentDay = true
                ),
                eventConfig = EventConfig(
                    showSubtitle = true,
                    showTimeStart = true,
                    showTimeEnd = true,
                    eventSpacingDp = 2
                ),
                actions = WeekViewActions(
                    onEventClick = { event ->
                        onEventClick(event.id)
                    }
                )
            )
        }
    }
}

@Preview(showBackground = true, name = "Week View - Light")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Week View - Dark")
@Composable
private fun TimetableWeekPreview() {
    BetterNeptunTheme {
        TimetablePreviewContent(viewMode = ViewMode.WEEK)
    }
}

@Preview(showBackground = true, name = "Day View")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Day View - Dark")
@Composable
private fun TimetableDayPreview() {
    BetterNeptunTheme {
        TimetablePreviewContent(viewMode = ViewMode.DAY)
    }
}

@Composable
private fun TimetablePreviewContent(viewMode: ViewMode) {
    val now = LocalDateTime.now()
    val today = LocalDate.now()
    val dateRange = when (viewMode) {
        ViewMode.WEEK -> {
            val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val friday = monday.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY))
            LocalDateRange(monday, friday)
        }
        ViewMode.DAY -> LocalDateRange(today, today)
    }
    TimetableContent(
        events = listOf(
            CalendarItem.Event(
                id = 1L,
                title = "Mobil szoftverfejlesztés",
                startTime = now.withHour(8).withMinute(30),
                endTime = now.withHour(10).withMinute(0),
                location = "IB.028",
                color = 0xFF4CAF50.toInt(),
                courseCode = "VIAUAC00",
                subjectCode = "VIAUAC00",
                teacher = "Dr. Egyetemi Tanár",
                isAllDay = false,
                isCanceled = false
            ),
            CalendarItem.LocalEvent(
                id = 2L,
                title = "Konzultáció",
                startTime = now.withHour(12).withMinute(30),
                endTime = now.withHour(14).withMinute(0),
                location = "Online",
                color = 0xFF2196F3.toInt()
            )
        ),
        times = "7:20",
        viewMode = viewMode,
        dateRange = dateRange,
        currentSelectedEvent = null,
        onNavigateBack = {},
        onViewModeChange = {},
        onNext = {},
        onPrevious = {},
        onEventClick = {},
        onDismissDetail = {},
        onDeleteEvent = {},
        onAddEvent = {}
    )
}
