package hu.kocsisgeri.betterneptun.ui.screen.timetable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import hu.kocsisgeri.betterneptun.common.utils.now
import hu.kocsisgeri.betterneptun.domain.model.neptun.CalendarItem
import hu.kocsisgeri.betterneptun.localization.LocalizationKey
import hu.kocsisgeri.betterneptun.ui.designsystem.R
import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
import hu.kocsisgeri.betterneptun.ui.navigation.modifier.isLandscape
import hu.kocsisgeri.betterneptun.ui.navigation.modifier.sharedBoundsAnimation
import hu.kocsisgeri.betterneptun.ui.screen.timetable.dialog.AddEventDialog
import hu.kocsisgeri.betterneptun.ui.screen.timetable.dialog.CourseDetailDialog
import hu.kocsisgeri.betterneptun.ui.screen.timetable.model.ViewMode
import hu.kocsisgeri.betterneptun.ui.screen.timetable.model.toComposeEvent
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.TimeTableView
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.components.MonthHeaderRow
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.config.EventConfig
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.config.WeekViewConfig
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.event.WeekData
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.event.WeekViewActions
import hu.kocsisgeri.betterneptun.ui.theme.BetterNeptunTheme
import hu.kocsisgeri.betterneptun.ui.theme.PreviewThemeProvider
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateRange
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
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
    val currentSelectedEvent by viewModel.selectedEvent.collectAsStateWithLifecycle()

    TimetableContent(
        sharedTransitionKey = initialId?.let {
            LocalizationKey.HOME_MENU_TIMETABLE.key + initialId.toString()
        } ?: LocalizationKey.HOME_MENU_TIMETABLE,
        viewMode = viewMode,
        currentSelectedEvent = currentSelectedEvent,
        onNavigateBack = navigator::navigateBack,
        onViewModeChange = viewModel::setViewMode,
        onEventClick = viewModel::selectEvent,
        onDismissDetail = viewModel::clearSelectedEvent,
        onDeleteEvent = viewModel::deleteEvent,
        onAddEvent = viewModel::addEvent,
        onGetWeekData = { page ->
            viewModel.getWeekDataForPage(page = page)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableContent(
    viewMode: ViewMode,
    currentSelectedEvent: CalendarItem?,
    onNavigateBack: () -> Unit,
    onViewModeChange: (ViewMode) -> Unit,
    onGetWeekData: (page: Int) -> WeekData,
    onEventClick: (Long) -> Unit,
    onDismissDetail: () -> Unit,
    onDeleteEvent: (Long) -> Unit,
    onAddEvent: (CalendarItem.LocalEvent) -> Unit,
    sharedTransitionKey: Any,
) {
    var showAddEventDialog by remember { mutableStateOf(false) }
    var initialDate by remember { mutableStateOf<LocalDateTime?>(null) }
    var currentMonthDate by remember { mutableStateOf<LocalDate?>(null) }

    CourseDetailDialog(
        selectedEvent = currentSelectedEvent,
        currentColor = currentSelectedEvent?.color ?: 0,
        onDismissRequest = onDismissDetail,
        onEditEvent = { showAddEventDialog = true },
        onDeleteLocalEvent = onDeleteEvent
    )

    AddEventDialog(
        show = showAddEventDialog,
        startDate = initialDate,
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
                viewMode = viewMode,
                currentMonthDate = currentMonthDate,
                onNavigateBack = onNavigateBack,
                onViewModeChange = onViewModeChange
            )
        },
        bottomBar = {
            val safePadding = WindowInsets.safeContent.asPaddingValues()

            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier
                    .padding(
                        start = safePadding.calculateStartPadding(LocalLayoutDirection.current),
                        end = safePadding.calculateEndPadding(LocalLayoutDirection.current),
                        bottom = BetterNeptunTheme.dimens.screenPadding,
                    )
                    .fillMaxWidth()
            ) {
                FloatingActionButton(
                    onClick = { showAddEventDialog = true },
                    containerColor = BetterNeptunTheme.colorScheme.primary,
                    contentColor = BetterNeptunTheme.colorScheme.onPrimary,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_event),
                        contentDescription = "Esemény hozzáadása"
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    start = paddingValues.calculateStartPadding(LocalLayoutDirection.current),
                    end = paddingValues.calculateEndPadding(LocalLayoutDirection.current),
                    bottom = BetterNeptunTheme.dimens.screenPadding
                )
                .padding(horizontal = BetterNeptunTheme.dimens.screenPadding)
                .clip(BetterNeptunTheme.shapes.large)
        ) {
            TimeTableView(
                weekData = onGetWeekData,
                weekViewConfig = WeekViewConfig(
                    showCurrentTimeIndicator = true,
                    highlightCurrentDay = true,
                    contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding())
                ),
                eventConfig = EventConfig(
                    showSubtitle = true,
                    showTimeStart = true,
                    showTimeEnd = true,
                    eventSpacingDp = 2,
                ),
                actions = WeekViewActions(
                    onEventClick = { event ->
                        onEventClick(event.id)
                    },
                    onTimeSlotClick = {
                        if (it == initialDate) showAddEventDialog = true

                        initialDate = it
                    },
                    onDateTitleChanged = {
                        currentMonthDate = it
                    }
                ),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeTableScreenTopBar(
    viewMode: ViewMode,
    onNavigateBack: () -> Unit,
    onViewModeChange: (ViewMode) -> Unit,
    currentMonthDate: LocalDate?
) {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Órarend",
                    style = BetterNeptunTheme.typography.titleLarge
                )
                if (isLandscape()) currentMonthDate?.let {
                    MonthHeaderRow(currentMonthDate)
                }
            }

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
                val newMode = ViewMode.entries.find {
                    viewMode.ordinal + 1 == it.ordinal
                } ?: ViewMode.WEEK
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
            val friday = monday.nextOrSame(DayOfWeek.SUNDAY)
            LocalDateRange(monday, friday)
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

    TimetableContent(
        sharedTransitionKey = Unit,
        viewMode = viewMode,
        currentSelectedEvent = null,
        onNavigateBack = {},
        onViewModeChange = {},
        onEventClick = {},
        onDismissDetail = {},
        onDeleteEvent = {},
        onAddEvent = {},
        onGetWeekData = {
            WeekData(
                dateRange,
                start = LocalTime(6, 0),
                end = LocalTime(23, 0)
            ).apply {
                events.filter { it.startTime.date in dateRange }.forEach {
                    add(it.toComposeEvent())
                }
            }
        }
    )
}
