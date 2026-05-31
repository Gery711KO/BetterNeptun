package hu.kocsisgeri.betterneptun.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.Grid
import androidx.compose.foundation.layout.GridTrackSize
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import hu.kocsisgeri.betterneptun.domain.model.TimeDuration
import hu.kocsisgeri.betterneptun.domain.model.neptun.ApiResult
import hu.kocsisgeri.betterneptun.domain.model.neptun.Avatar
import hu.kocsisgeri.betterneptun.domain.model.neptun.StudentData
import hu.kocsisgeri.betterneptun.localization.LocalizationKey
import hu.kocsisgeri.betterneptun.localization.localized
import hu.kocsisgeri.betterneptun.ui.R
import hu.kocsisgeri.betterneptun.ui.core.Navigator
import hu.kocsisgeri.betterneptun.ui.core.composable.AvatarImage
import hu.kocsisgeri.betterneptun.ui.core.composable.measure.SizeMeasurer
import hu.kocsisgeri.betterneptun.ui.core.composable.measure.SizeMeasurerScope
import hu.kocsisgeri.betterneptun.ui.core.permission.PermissionHandler
import hu.kocsisgeri.betterneptun.ui.core.permission.model.PermissionData
import hu.kocsisgeri.betterneptun.ui.core.permission.model.PermissionDisclaimer
import hu.kocsisgeri.betterneptun.ui.core.permission.rememberPermissionLauncher
import hu.kocsisgeri.betterneptun.ui.core.theme.BetterNeptunTheme
import hu.kocsisgeri.betterneptun.ui.destination.MessagesDestination
import hu.kocsisgeri.betterneptun.ui.destination.SemestersDestination
import hu.kocsisgeri.betterneptun.ui.destination.SettingsDestination
import hu.kocsisgeri.betterneptun.ui.destination.SubjectsDestination
import hu.kocsisgeri.betterneptun.ui.destination.TimetableDestination
import hu.kocsisgeri.betterneptun.ui.screen.home.model.CurrentCourseDetail
import hu.kocsisgeri.betterneptun.ui.screen.home.model.NextCourseDetail
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.time.LocalDateTime

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    navigator: Navigator = koinInject(),
) {
    val permissionHandler: PermissionHandler = koinInject()
    val launcher = rememberPermissionLauncher(permissionHandler)

    val studentData by viewModel.studentData.collectAsStateWithLifecycle()
    val unreadMessages by viewModel.unreadMessages.collectAsStateWithLifecycle()

    val currentCourses by viewModel.currentCourses.collectAsStateWithLifecycle()
    val nextCourseState by viewModel.nextCourse.collectAsStateWithLifecycle()
    val refreshProgress by viewModel.refreshProgress.collectAsState(initial = null)

    val permissions by permissionHandler.permissions.collectAsStateWithLifecycle()

    HomeContent(
        studentData = studentData,
        unreadMessages = unreadMessages ?: 0,
        currentCourses = currentCourses,
        nextCourseState = nextCourseState,
        permissions = permissions,
        refreshProgress = refreshProgress,
        onRefresh = { viewModel.refreshData() },
        onLaunchPermissionRequest = {
            launcher.launch(it)
        },
        onNavigate = { navigator.navigateTo(it) }
    )
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalGridApi::class)
@Composable
private fun HomeContent(
    studentData: StudentData?,
    unreadMessages: Int,
    currentCourses: List<CurrentCourseDetail>,
    nextCourseState: NextCourseDetail?,
    permissions: List<PermissionData>,
    refreshProgress: ApiResult<Unit>?,
    onLaunchPermissionRequest: (PermissionData) -> Unit,
    onRefresh: () -> Unit,
    onNavigate: (NavKey) -> Unit,
) {
    val isRefreshing = refreshProgress is ApiResult.Loading
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = onRefresh
    )

    val visibleDisclaimers by remember(permissions) {
        derivedStateOf {
            permissions.filter {
                it.permissionState != PermissionData.State.Granted
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .pullRefresh(pullRefreshState),
    ) { padding ->
        Box {
            LazyColumn(
                contentPadding = padding,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp)
            ) {
                item("HEADER") {
                    Header(
                        studentData = studentData,
                        unreadMessages = unreadMessages,
                        onNavigateToScreen = onNavigate,
                        modifier = Modifier.animateItem(),
                    )
                }
                if (visibleDisclaimers.isNotEmpty()) item("DISCLAIMERS") {
                    PermissionDisclaimerCarousel(
                        permissions = visibleDisclaimers,
                        onLaunchPermissionRequest = onLaunchPermissionRequest,
                        modifier = Modifier.animateItem(),
                    )
                }
                if (currentCourses.isNotEmpty()) item("ONGOING_COURSE") {
                    CurrentlyOngoingCourses(
                        currentCourses = currentCourses,
                        modifier = Modifier.animateItem(),
                        onCourseClick = { onNavigate(TimetableDestination(it)) }
                    )
                }
                if (nextCourseState != null) item("NEXT_COURSE") {
                    NextCourseCard(
                        course = nextCourseState,
                        modifier = Modifier.animateItem(),
                        onCourseClick = { onNavigate(TimetableDestination(it)) }
                    )
                }
                item("NAVIGATION_GRID") {
                    NavigationGrid(
                        onNavigate = onNavigate,
                        modifier = Modifier.animateItem()
                    )
                }
            }

            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefreshState,
                modifier = Modifier
                    .padding(padding)
                    .align(Alignment.TopCenter),
                backgroundColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun PermissionDisclaimerCarousel(
    permissions: List<PermissionData>,
    modifier: Modifier = Modifier,
    onLaunchPermissionRequest: (PermissionData) -> Unit,
) {
    val lazyListState = rememberPagerState { permissions.size }

    if (permissions.isNotEmpty()) {
        SizeMeasurer {
            HorizontalPager(
                pageSpacing = 10.dp,
                contentPadding = PaddingValues(horizontal = 16.dp),
                state = lazyListState,
                modifier = modifier.fillMaxWidth(),
            ) { page ->
                permissions[page].let { permission ->
                    PermissionDisclaimerCard(
                        disclaimer = permission.disclaimer,
                        onRequest = { onLaunchPermissionRequest(permission) },
                        modifier = Modifier.fillAvailableSpace()
                    )
                }
            }
        }
    }
}

@Composable
private fun SizeMeasurerScope.PermissionDisclaimerCard(
    disclaimer: PermissionDisclaimer,
    onRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = disclaimer.humanReadablePermissionName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = disclaimer.disclaimer,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Spacer(Modifier.weight(1f, isSizeMeasured))
            Button(
                onClick = onRequest,
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = localized(LocalizationKey.HOME_PERMISSION_PERMIT),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalGridApi::class)
private fun NavigationGrid(
    modifier: Modifier = Modifier,
    onNavigate: (NavKey) -> Unit
) {
    Grid(
        config = {
            columns(
                GridTrackSize.Percentage(0.5f),
                GridTrackSize.Percentage(0.5f),
            )
            rows(GridTrackSize.MaxContent)

            gap(8.dp)
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        NavButton(
            icon = painterResource(id = R.drawable.ic_mail),
            text = localized(LocalizationKey.HOME_MENU_MESSAGES),
            onClick = { onNavigate(MessagesDestination) }
        )
        NavButton(
            icon = painterResource(id = R.drawable.ic_calendar),
            text = localized(LocalizationKey.HOME_MENU_TIMETABLE),
            onClick = { onNavigate(TimetableDestination(null)) }
        )
        NavButton(
            icon = painterResource(id = R.drawable.ic_courses),
            text = localized(LocalizationKey.HOME_MENU_COURSES),
            onClick = { onNavigate(SubjectsDestination) }
        )
        NavButton(
            icon = painterResource(id = R.drawable.ic_semesters),
            text = localized(LocalizationKey.HOME_MENU_SEMESTERS),
            onClick = { onNavigate(SemestersDestination) }
        )
        NavButton(
            icon = painterResource(id = R.drawable.ic_exams),
            text = localized(LocalizationKey.HOME_MENU_EXAMS),
            isEnabled = false,
            disabledTag = localized(LocalizationKey.HOME_LABEL_UNDERDEVELOPMENT),
            onClick = { /* TODO */ }
        )
        NavButton(
            icon = painterResource(id = R.drawable.ic_schedule),
            text = localized(LocalizationKey.HOME_MENU_PERIODS),
            isEnabled = false,
            disabledTag = localized(LocalizationKey.HOME_LABEL_UNDERDEVELOPMENT),
            onClick = { /* TODO */ }
        )
    }
}

@Composable
private fun CurrentlyOngoingCourses(
    currentCourses: List<CurrentCourseDetail>,
    modifier: Modifier = Modifier,
    onCourseClick: (id: Long) -> Unit,
) {
    if (currentCourses.isNotEmpty()) {
        val listState = rememberLazyListState()

        LazyRow(
            modifier = modifier.fillMaxWidth(),
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(horizontal = 12.dp),
            flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
        ) {
            items(currentCourses) { course ->
                CurrentCourseItem(
                    course = course,
                    modifier = Modifier.fillParentMaxWidth(),
                    onCourseClick = onCourseClick
                )
            }
        }
    }
}

@Composable
private fun Header(
    studentData: StudentData?,
    unreadMessages: Int,
    modifier: Modifier = Modifier,
    onNavigateToScreen: (NavKey) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .height(intrinsicSize = IntrinsicSize.Max),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(end = 10.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                studentData?.avatar?.let {
                    AvatarImage(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape),
                        avatar = studentData.avatar
                    )
                    Spacer(Modifier.width(12.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = studentData?.name ?: "",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = studentData?.neptun ?: "",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
                if (unreadMessages > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = unreadMessages.toString(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_mail),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f),
            shape = RoundedCornerShape(20.dp),
            onClick = { onNavigateToScreen(SettingsDestination) },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
fun CurrentCourseItem(
    course: CurrentCourseDetail,
    modifier: Modifier = Modifier,
    onCourseClick: (id: Long) -> Unit,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        onClick = {
            onCourseClick(course.id)
        }
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = localized(LocalizationKey.HOME_ONGOING_COURSE),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                LinearProgressIndicator(
                    progress = { course.progress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = Color(course.color),
                    trackColor = Color(course.color).copy(alpha = 0.2f),
                    strokeCap = StrokeCap.Round
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ic_course),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = course.title.trim(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    if (course.location.isNullOrBlank().not()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.ic_location),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = course.location.trim(),
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_schedule),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = localized(
                            LocalizationKey.HOME_ONGOING_COURSE_MINUTES,
                            course.remainingTimeMinutes.toString()
                        ),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun NextCourseCard(
    course: NextCourseDetail?,
    modifier: Modifier = Modifier,
    onCourseClick: (id: Long) -> Unit,
) {
    course?.let {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            onClick = {
                onCourseClick(it.id)
            },
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 20.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.height(intrinsicSize = IntrinsicSize.Max),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = localized(LocalizationKey.HOME_NEXT_COURSE),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Event,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (course.timeUntilEvent.unit) {
                                    TimeDuration.Unit.MINUTES -> localized(
                                        LocalizationKey.HOME_NEXT_COURSE_MINUTES,
                                        course.timeUntilEvent.value.toString()
                                    )

                                    TimeDuration.Unit.HOURS -> localized(
                                        LocalizationKey.HOME_NEXT_COURSE_HOURS,
                                        course.timeUntilEvent.value.toString()
                                    )

                                    TimeDuration.Unit.DAYS -> localized(
                                        LocalizationKey.HOME_NEXT_COURSE_DAYS,
                                        course.timeUntilEvent.value.toString()
                                    )
                                },
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_course),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = course.title.trim(),
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        if (course.location.isNullOrBlank().not()) {
                            Spacer(modifier = Modifier.height(4.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_location),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = course.location.trim(),
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
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
                            text = "${course.startTime.hour}:${
                                course.startTime.minute.toString().padStart(2, '0')
                            }",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${course.endTime.hour}:${
                                course.endTime.minute.toString().padStart(2, '0')
                            }",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .width(6.dp)
                            .fillMaxHeight()
                            .padding(vertical = 12.dp)
                            .background(Color(course.color), CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
fun NavButton(
    modifier: Modifier = Modifier,
    icon: Painter,
    text: String,
    isEnabled: Boolean = true,
    disabledTag: String? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        enabled = isEnabled,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
            disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
            disabledTag?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(
                        alpha = 0.5f
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    BetterNeptunTheme {
        val currentCourse = CurrentCourseDetail(
            id = 11,
            title = "Mobil szoftverfejlesztés",
            location = "BA.F.01",
            color = 0xFF4285F4.toInt(),
            progress = 30,
            remainingTimeMinutes = 30
        )

        val nextCourse = NextCourseDetail(
            id = 16,
            title = "Full stack fejlesztés",
            startTime = LocalDateTime.now().plusHours(1),
            endTime = LocalDateTime.now().plusHours(3),
            location = "BA.F.02",
            color = 0xFF4285F4.toInt(),
            timeUntilEvent = TimeDuration(
                value = 1,
                unit = TimeDuration.Unit.DAYS
            )
        )

        HomeContent(
            studentData = StudentData(
                name = "Példa János",
                neptun = "ABC123",
                avatar = Avatar.MonogramAvatar(
                    monogram = "PJ",
                    colorLong = 0xFF4285F4
                )
            ),
            unreadMessages = 5,
            currentCourses = listOf(currentCourse),
            nextCourseState = nextCourse,
            refreshProgress = null,
            permissions = emptyList(),
            onRefresh = {},
            onLaunchPermissionRequest = {},
            onNavigate = {}
        )
    }
}
