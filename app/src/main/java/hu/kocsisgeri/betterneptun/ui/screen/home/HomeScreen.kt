package hu.kocsisgeri.betterneptun.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.Fr
import androidx.compose.foundation.layout.Grid
import androidx.compose.foundation.layout.GridTrackSize
import androidx.compose.foundation.layout.GridTrackSpec
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.columns
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.rows
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import hu.kocsisgeri.betterneptun.R
import hu.kocsisgeri.betterneptun.data.dao.ApiResult
import hu.kocsisgeri.betterneptun.data.repository.course.HomeState
import hu.kocsisgeri.betterneptun.domain.model.StudentData
import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
import hu.kocsisgeri.betterneptun.ui.navigation.destination.MessagesDestination
import hu.kocsisgeri.betterneptun.ui.navigation.destination.SemestersDestination
import hu.kocsisgeri.betterneptun.ui.navigation.destination.SettingsDestination
import hu.kocsisgeri.betterneptun.ui.navigation.destination.SubjectsDestination
import hu.kocsisgeri.betterneptun.ui.navigation.destination.TimetableDestination
import hu.kocsisgeri.betterneptun.ui.theme.BetterNeptunTheme
import hu.kocsisgeri.betterneptun.ui.screen.timetable.model.CalendarEntity
import hu.kocsisgeri.betterneptun.utils.getCourseDateString
import hu.kocsisgeri.betterneptun.utils.getPercent
import hu.kocsisgeri.betterneptun.utils.getTimeLeft
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.time.LocalDateTime

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    navigator: Navigator = koinInject()
) {
    val studentData by viewModel.studentData.observeAsState()
    val unreadMessages by viewModel.unreadMessages.observeAsState(0)
    val currentCourses by viewModel.currentCourses.collectAsStateWithLifecycle()
    val nextCourseState by HomeState.nextCourse.observeAsState()
    val refreshProgress by viewModel.refreshProgress.collectAsState(initial = null)

    LaunchedEffect(Unit) {
        if (!viewModel.isLoggedIn.value) {
            viewModel.fetchData()
        }
    }

    HomeContent(
        studentData = studentData,
        unreadMessages = unreadMessages,
        currentCourses = currentCourses,
        nextCourseState = nextCourseState,
        refreshProgress = refreshProgress,
        onRefresh = { viewModel.refreshData() },
        onNavigate = { navigator.navigateTo(it) }
    )
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalGridApi::class)
@Composable
fun HomeContent(
    studentData: StudentData?,
    unreadMessages: Int,
    currentCourses: List<CalendarEntity.Event>,
    nextCourseState: ApiResult<CalendarEntity.Event>?,
    refreshProgress: ApiResult<Unit>?,
    onRefresh: () -> Unit,
    onNavigate: (NavKey) -> Unit,
) {
    val isRefreshing = refreshProgress is ApiResult.Progress
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = onRefresh
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.add(
            WindowInsets(10.dp, 10.dp, 10.dp, 10.dp)
        ),
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .pullRefresh(pullRefreshState),
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 16.dp)
            ) {
                Header(
                    studentData = studentData,
                    unreadMessages = unreadMessages,
                    onNavigateToScreen = onNavigate
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Current Courses
                if (currentCourses.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(currentCourses) { course ->
                            CurrentCourseItem(
                                course = course,
                                modifier = Modifier.fillParentMaxWidth()
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Next Course
                NextCourseCard(nextCourseState)

                Spacer(modifier = Modifier.height(16.dp))

                // Navigation Buttons Grid
                Grid(
                    modifier = Modifier.fillMaxWidth(),
                    config = {
                        columns(
                            GridTrackSize.Percentage(0.5f),
                            GridTrackSize.Percentage(0.5f),
                        )
                        rows(
                            GridTrackSize.MaxContent,
                        )

                        gap(8.dp)
                    }
                ) {
                    NavButton(
                        modifier = Modifier.gridItem(row = 1, column =  1),
                        icon = painterResource(id = R.drawable.ic_mail),
                        text = "Üzenetek",
                        onClick = { onNavigate(MessagesDestination) }
                    )
                    NavButton(
                        modifier = Modifier.gridItem(row = 1, column =  2),
                        icon = painterResource(id = R.drawable.ic_calendar),
                        text = "Órarend",
                        onClick = { onNavigate(TimetableDestination) }
                    )

                    NavButton(
                        modifier = Modifier.gridItem(row = 2, column =  1),
                        icon = painterResource(id = R.drawable.ic_courses),
                        text = "Kurzusok",
                        onClick = { onNavigate(SubjectsDestination) }
                    )
                    NavButton(
                        modifier = Modifier.gridItem(row = 2, column =  2),
                        icon = painterResource(id = R.drawable.ic_exams),
                        text = "Vizsgák",
                        onClick = { /* TODO */ }
                    )
                    NavButton(
                        modifier = Modifier.gridItem(row = 3, column =  1),
                        icon = painterResource(id = R.drawable.ic_semesters),
                        text = "Félévek",
                        onClick = { onNavigate(SemestersDestination) }
                    )
                    NavButton(
                        modifier = Modifier.gridItem(row = 3, column =  2),
                        icon = painterResource(id = R.drawable.ic_schedule),
                        text = "Időszakok",
                        onClick = { /* TODO */ }
                    )
                }
            }

            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
private fun Header(
    studentData: StudentData?,
    unreadMessages: Int,
    onNavigateToScreen: (NavKey) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(intrinsicSize = IntrinsicSize.Max),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(end = 10.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = studentData?.name ?: "",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = studentData?.neptun ?: "",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                }
                if (unreadMessages > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = unreadMessages.toString(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_mail),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .clickable { onNavigateToScreen(SettingsDestination) },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun CurrentCourseItem(
    course: CalendarEntity.Event,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Éppen tart",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                LinearProgressIndicator(
                    progress = { course.getPercent() / 100f },
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    color = Color(course.color),
                    trackColor = Color(course.color).copy(alpha = 0.2f),
                    strokeCap = StrokeCap.Round
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_course),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = course.title.toString().trim(),
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                }
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ic_location),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = course.location.toString().trim(),
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ic_schedule),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = course.endTime.getTimeLeft(),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NextCourseCard(state: ApiResult<CalendarEntity.Event>?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                is ApiResult.Progress -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(50.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                is ApiResult.Success -> {
                    val event = state.data
                    Row(
                        modifier = Modifier.height(intrinsicSize = IntrinsicSize.Max),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Következő óra",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Event,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = event.startTime.getCourseDateString(),
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_course),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = event.title.toString().trim(),
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_location),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = event.location.toString().trim(),
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                )
                            }
                        }
                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(vertical = 12.dp)
                        ) {
                            Text(
                                text = "${event.startTime.hour}:${event.startTime.minute.toString().padStart(2, '0')}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "${event.endTime.hour}:${event.endTime.minute.toString().padStart(2, '0')}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .fillMaxHeight()
                                .padding(vertical = 12.dp)
                                .background(Color(event.color), CircleShape)
                        )
                    }
                }
                else -> { /* Handle Error or Nothing */ }
            }
        }
    }
}

@Composable
fun NavButton(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.painter.Painter,
    text: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    BetterNeptunTheme {
        val mockEvent = CalendarEntity.Event(
            id = 1,
            title = "Mobil szoftverfejlesztés",
            courseCode = "MSF123",
            subjectCode = "SUB456",
            teacher = "Dr. Kovács Béla",
            startTime = LocalDateTime.now().plusHours(1),
            endTime = LocalDateTime.now().plusHours(3),
            location = "BA.F.01",
            color = 0xFF4285F4.toInt(),
            isAllDay = false,
            isCanceled = false
        )

        HomeContent(
            studentData = StudentData("Példa János", "ABC123"),
            unreadMessages = 5,
            currentCourses = listOf(mockEvent.copy(title = "Éppen zajló óra", startTime = LocalDateTime.now().minusHours(1))),
            nextCourseState = ApiResult.Success(mockEvent),
            refreshProgress = null,
            onRefresh = {},
            onNavigate = {}
        )
    }
}
