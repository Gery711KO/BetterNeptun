package hu.kocsisgeri.betterneptun.ui.screen.timetable

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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.tobiasschuerg.weekview.compose.style.defaultWeekViewColors
import hu.kocsisgeri.betterneptun.common.utils.now
import hu.kocsisgeri.betterneptun.domain.model.neptun.CalendarItem
import hu.kocsisgeri.betterneptun.localization.LocalizationKey
import hu.kocsisgeri.betterneptun.ui.R
import hu.kocsisgeri.betterneptun.ui.core.modifier.sharedBoundsAnimation
import hu.kocsisgeri.betterneptun.ui.core.theme.BetterNeptunTheme
import hu.kocsisgeri.betterneptun.ui.core.theme.PreviewThemeProvider
import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
import hu.kocsisgeri.betterneptun.ui.screen.timetable.dialog.AddEventDialog
import hu.kocsisgeri.betterneptun.ui.screen.timetable.dialog.CourseDetailDialog
import hu.kocsisgeri.betterneptun.ui.screen.timetable.model.ViewMode
import hu.kocsisgeri.betterneptun.ui.screen.timetable.model.toComposeEvent
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.TimeTableView
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.config.EventConfig
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.config.WeekViewConfig
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.event.WeekData
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.event.WeekDataDateRange
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.event.WeekViewActions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateRange
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atTime
import kotlinx.datetime.nextOrSame
import kotlinx.datetime.previousOrSame
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

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
        sharedTransitionKey = initialId?.let {
            LocalizationKey.HOME_MENU_TIMETABLE.key + initialId.toString()
        } ?: LocalizationKey.HOME_MENU_TIMETABLE,
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
    sharedTransitionKey: Any,
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
        modifier = Modifier.sharedBoundsAnimation(sharedTransitionKey),
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
                containerColor = BetterNeptunTheme.colorScheme.primary,
                contentColor = BetterNeptunTheme.colorScheme.onPrimary
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
                            .padding(horizontal = BetterNeptunTheme.dimens.screenPadding)
                            .clip(BetterNeptunTheme.shapes.large)
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
                style = BetterNeptunTheme.typography.titleLarge
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
                } ?: ViewMode.FULL_WEEK
                onViewModeChange(newMode)
            }) {
                Icon(
                    painter = painterResource(id = viewMode.icon),
                    contentDescription = "Nézet váltás",
                    modifier = Modifier.size(BetterNeptunTheme.dimens.iconMedium)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BetterNeptunTheme.colorScheme.background,
            navigationIconContentColor = BetterNeptunTheme.colorScheme.onBackground,
            titleContentColor = BetterNeptunTheme.colorScheme.onBackground,
            actionIconContentColor = BetterNeptunTheme.colorScheme.onBackground
        )
    )
}

@PreviewLightDark
@PreviewWrapper(PreviewThemeProvider::class)
@Composable
private fun TimetableWeekPreview() {
    TimetablePreviewContent(viewMode = ViewMode.WEEK)
}

@PreviewLightDark
@PreviewWrapper(PreviewThemeProvider::class)
@Composable
private fun TimetableDayPreview() {
    TimetablePreviewContent(viewMode = ViewMode.DAY)
}

@Composable
private fun TimetablePreviewContent(viewMode: ViewMode) {
    val now = LocalDateTime.now()
    val today = LocalDateTime.now()
    val dateRange = when (viewMode) {
        ViewMode.WEEK -> {
            val monday = today.date.previousOrSame(DayOfWeek.MONDAY)
            val friday = monday.nextOrSame(DayOfWeek.FRIDAY)
            LocalDateRange(monday, friday)
        }

        ViewMode.FULL_WEEK -> {
            val monday = today.date.previousOrSame(DayOfWeek.MONDAY)
            val sunday = monday.nextOrSame(DayOfWeek.SUNDAY)
            LocalDateRange(monday, sunday)
        }

        ViewMode.DAY -> LocalDateRange(today.date, today.date)
    }
    val events = listOf(
        CalendarItem.Event(
            id = 1L,
            title = "Mobil szoftverfejlesztés",
            startTime = LocalDateTime(now.date, LocalTime(8, 30)),
            endTime = LocalDateTime(now.date, LocalTime(10, 0)),
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
            startTime = LocalDateTime(now.date, LocalTime(12, 30)),
            endTime = LocalDateTime(now.date, LocalTime(14, 0)),
            location = "Online",
            color = 0xFF2196F3.toInt()
        )
    )

    defaultWeekViewColors()

    TimetableContent(
        sharedTransitionKey = Unit,
        viewMode = viewMode,
        weeks = listOf(
            WeekData(
                dateRange,
                start = LocalTime(6, 0),
                end = LocalTime(23, 0)
            ).apply {
                events.filter { it.startTime.date in dateRange }.forEach {
                    add(it.toComposeEvent())
                }
            }
        ),
        currentSelectedEvent = null,
        onNavigateBack = {},
        onViewModeChange = {},
        onNext = {},
        onPrevious = {},
        onEventClick = {},
        onDismissDetail = {},
        onDeleteEvent = {},
        onAddEvent = {},
    )
}
