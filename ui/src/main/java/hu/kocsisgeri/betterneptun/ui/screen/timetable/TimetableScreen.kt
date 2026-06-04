package hu.kocsisgeri.betterneptun.ui.screen.timetable

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import hu.kocsisgeri.betterneptun.domain.model.neptun.CalendarItem
import hu.kocsisgeri.betterneptun.ui.R
import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
import hu.kocsisgeri.betterneptun.ui.core.theme.BetterNeptunTheme
import hu.kocsisgeri.betterneptun.ui.screen.timetable.dialog.AddEventDialog
import hu.kocsisgeri.betterneptun.ui.screen.timetable.dialog.CourseDetailDialog
import hu.kocsisgeri.betterneptun.ui.screen.timetable.model.ViewMode
import hu.kocsisgeri.betterneptun.ui.screen.timetable.model.toComposeEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters

@Composable
fun TimetableScreen(
    initialId: Long? = null,
    viewModel: TimetableViewModel = koinViewModel { parametersOf(initialId) },
    navigator: Navigator = koinInject()
) {
    val viewMode by viewModel.viewMode.collectAsStateWithLifecycle()
    val weeks by viewModel.weeks.collectAsStateWithLifecycle()
    val currentSelectedEvent by viewModel.selectedEvent.collectAsStateWithLifecycle()

    TimetableContent(
        viewMode = viewMode,
        weeks = weeks,
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
    viewMode: ViewMode,
    weeks: List<WeekData>,
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
    val coroutineScope = rememberCoroutineScope()
    val pagerState = if (weeks.isNotEmpty()) {
        rememberPagerState(1) { weeks.size }
    } else {
        null
    }

    InfinitePagerHandler(
        weeks = weeks,
        pagerState = pagerState,
        onPrevious = onPrevious,
        onNext = onNext
    )

    CourseDetailDialog(
        selectedEvent = currentSelectedEvent,
        currentColor = currentSelectedEvent?.color ?: 0,
        onDismissRequest = onDismissDetail,
        onEditEvent = { showAddEventDialog = true },
        onDeleteLocalEvent = onDeleteEvent
    )

    AddEventDialog(
        show = showAddEventDialog,
        event = currentSelectedEvent as? CalendarItem.LocalEvent,
        onDismissRequest = {
            showAddEventDialog = false
            onDismissDetail()
        },
        onAddEvent = onAddEvent
    )

    Scaffold(
        topBar = {
            TimeTableScreenTopBar(
                pagerState = pagerState,
                viewMode = viewMode,
                coroutineScope = coroutineScope,
                onNavigateBack = onNavigateBack,
                onViewModeChange = onViewModeChange
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddEventDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_event),
                    contentDescription = "Esemény hozzáadása"
                )
            }
        }
    ) { paddingValues ->
        pagerState?.let {
            HorizontalPager(
                state = pagerState,
                key = { page -> weeks.getOrNull(page)?.dateRange?.toString() ?: page }
            ) { page ->
                weeks.getOrNull(page)?.let { weekData ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(horizontal = 16.dp)
                            .clip(MaterialTheme.shapes.large)
                    ) {
                        TimeTableView(
                            weekData = weekData,
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
                            ),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfinitePagerHandler(
    pagerState: PagerState?,
    weeks: List<WeekData>,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    LaunchedEffect(pagerState?.currentPage) {
        if (weeks.size == 3) {
            when (pagerState?.currentPage) {
                0 -> onPrevious()
                2 -> onNext()
            }
        }
    }

    LaunchedEffect(pagerState?.settledPage) {
        if (pagerState?.settledPage != 1) {
            pagerState?.scrollToPage(1)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeTableScreenTopBar(
    pagerState: PagerState?,
    coroutineScope: CoroutineScope,
    viewMode: ViewMode,
    onNavigateBack: () -> Unit,
    onViewModeChange: (ViewMode) -> Unit
) {
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
            IconButton(onClick = {
                coroutineScope.launch {
                    pagerState?.animateScrollToPage(0)
                }
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Előző"
                )
            }
            IconButton(onClick = {
                coroutineScope.launch {
                    pagerState?.animateScrollToPage(2)
                }
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Következő"
                )
            }
            IconButton(onClick = {
                val newMode = ViewMode.entries.find {
                    viewMode.ordinal + 1 == it.ordinal
                }?: ViewMode.FULL_WEEK
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
        ViewMode.FULL_WEEK -> {
            val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val friday = monday.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
            LocalDateRange(monday, friday)
        }
        ViewMode.DAY -> LocalDateRange(today, today)
    }
    val events = listOf(
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
    )

    TimetableContent(
        weeks = listOf(
            WeekData(
                dateRange,
                start = LocalTime.of(6, 0),
                end = LocalTime.of(23, 0)
            ).apply {
                events.filter { it.startTime.toLocalDate() in dateRange }.forEach {
                    add(it.toComposeEvent())
                }
            }
        ),
        viewMode = viewMode,
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
