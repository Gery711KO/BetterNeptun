package hu.kocsisgeri.betterneptun.ui.screen.home

import android.content.res.Configuration
import androidx.annotation.DrawableRes
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import hu.kocsisgeri.betterneptun.common.utils.now
import hu.kocsisgeri.betterneptun.common.utils.plus
import hu.kocsisgeri.betterneptun.domain.model.TimeDuration
import hu.kocsisgeri.betterneptun.domain.model.UiResult
import hu.kocsisgeri.betterneptun.domain.model.neptun.Avatar
import hu.kocsisgeri.betterneptun.domain.model.neptun.StudentData
import hu.kocsisgeri.betterneptun.localization.LocalizationKey
import hu.kocsisgeri.betterneptun.localization.localized
import hu.kocsisgeri.betterneptun.ui.core.permission.PermissionHandler
import hu.kocsisgeri.betterneptun.ui.core.permission.model.PermissionData
import hu.kocsisgeri.betterneptun.ui.core.permission.model.PermissionDisclaimer
import hu.kocsisgeri.betterneptun.ui.core.permission.rememberPermissionLauncher
import hu.kocsisgeri.betterneptun.ui.designsystem.R
import hu.kocsisgeri.betterneptun.ui.designsystem.composable.AvatarImage
import hu.kocsisgeri.betterneptun.ui.designsystem.composable.measure.SizeMeasurer
import hu.kocsisgeri.betterneptun.ui.designsystem.composable.measure.SizeMeasurerScope
import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
import hu.kocsisgeri.betterneptun.ui.navigation.destination.MessagesDestination
import hu.kocsisgeri.betterneptun.ui.navigation.destination.SemestersDestination
import hu.kocsisgeri.betterneptun.ui.navigation.destination.SettingsDestination
import hu.kocsisgeri.betterneptun.ui.navigation.destination.SubjectsDestination
import hu.kocsisgeri.betterneptun.ui.navigation.destination.TimetableDestination
import hu.kocsisgeri.betterneptun.ui.navigation.modifier.hideFromTransition
import hu.kocsisgeri.betterneptun.ui.navigation.modifier.isLandscape
import hu.kocsisgeri.betterneptun.ui.navigation.modifier.sharedBoundsAnimation
import hu.kocsisgeri.betterneptun.ui.screen.home.model.CurrentCourseDetail
import hu.kocsisgeri.betterneptun.ui.screen.home.model.NextCourseDetail
import hu.kocsisgeri.betterneptun.ui.theme.BetterNeptunTheme
import hu.kocsisgeri.betterneptun.ui.theme.PreviewThemeProvider
import kotlinx.datetime.LocalDateTime
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import kotlin.time.Duration.Companion.hours

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
        onDismissPermissionRequest = {
            permissionHandler.dismissPermission(it)
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
    refreshProgress: UiResult<Unit>?,
    onLaunchPermissionRequest: (PermissionData) -> Unit,
    onDismissPermissionRequest: (PermissionData) -> Unit,
    onRefresh: () -> Unit,
    onNavigate: (NavKey) -> Unit,
) {
    val isLandscape = isLandscape()
    val isRefreshing = refreshProgress is UiResult.Loading
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
        containerColor = BetterNeptunTheme.colorScheme.background,
        modifier = Modifier.pullRefresh(pullRefreshState),
    ) { padding ->
        Box {
            LazyColumn(
                contentPadding = padding,
                verticalArrangement = Arrangement.spacedBy(BetterNeptunTheme.dimens.groupSpacing),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = BetterNeptunTheme.dimens.groupSpacing)
            ) {
                item("HEADER") {
                    Header(
                        studentData = studentData,
                        unreadMessages = unreadMessages,
                        onNavigateToScreen = onNavigate,
                        modifier = Modifier.animateItem(),
                    )
                }

                if (visibleDisclaimers.isNotEmpty()) {
                    item("DISCLAIMERS") {
                        PermissionDisclaimerCarousel(
                            permissions = visibleDisclaimers,
                            onLaunchPermissionRequest = onLaunchPermissionRequest,
                            onDismissPermissionRequest = onDismissPermissionRequest,
                            modifier = Modifier.animateItem(),
                        )
                    }
                }

                if (isLandscape) {
                    item("BODY") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(BetterNeptunTheme.dimens.groupSpacing)
                        ) {
                            Column(
                                modifier = Modifier.weight(2/5f),
                                verticalArrangement = Arrangement.spacedBy(BetterNeptunTheme.dimens.groupSpacing)
                            ) {
                                CurrentlyOngoingCourses(
                                    currentCourses = currentCourses,
                                    onCourseClick = { onNavigate(TimetableDestination(it)) }
                                )
                                NextCourseCard(
                                    course = nextCourseState,
                                    onCourseClick = { onNavigate(TimetableDestination(it)) }
                                )
                            }
                            NavigationGrid(
                                modifier = Modifier.weight(3/5f),
                                columns = 3,
                                onNavigate = onNavigate
                            )
                        }
                    }
                } else {
                    item("ONGOING_COURSE") {
                        CurrentlyOngoingCourses(
                            currentCourses = currentCourses,
                            modifier = Modifier.animateItem(),
                            onCourseClick = { onNavigate(TimetableDestination(it)) }
                        )
                    }
                    item("NEXT_COURSE") {
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
            }

            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefreshState,
                modifier = Modifier
                    .padding(padding)
                    .align(Alignment.TopCenter),
                backgroundColor = BetterNeptunTheme.colorScheme.surface,
                contentColor = BetterNeptunTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun PermissionDisclaimerCarousel(
    permissions: List<PermissionData>,
    modifier: Modifier = Modifier,
    onLaunchPermissionRequest: (PermissionData) -> Unit,
    onDismissPermissionRequest: (PermissionData) -> Unit,
) {
    val lazyListState = rememberPagerState { permissions.size }

    if (permissions.isNotEmpty()) {
        SizeMeasurer {
            HorizontalPager(
                pageSpacing = BetterNeptunTheme.dimens.paddingSmall,
                contentPadding = PaddingValues(horizontal = BetterNeptunTheme.dimens.itemSpacing),
                state = lazyListState,
                modifier = modifier.fillMaxWidth(),
            ) { page ->
                permissions[page].let { permission ->
                    PermissionDisclaimerCard(
                        disclaimer = permission.disclaimer,
                        onRequest = { onLaunchPermissionRequest(permission) },
                        onDismiss = { onDismissPermissionRequest(permission) },
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
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Card(
        modifier = modifier,
        shape = BetterNeptunTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = BetterNeptunTheme.colorScheme.tertiaryContainer,
            contentColor = BetterNeptunTheme.colorScheme.onTertiaryContainer
        )
    ) {
        if (isLandscape) {
            Row(
                modifier = Modifier
                    .padding(BetterNeptunTheme.dimens.paddingLarge)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(BetterNeptunTheme.dimens.paddingMedium)
            ) {
                PermissionDisclaimerContent(
                    title = disclaimer.humanReadablePermissionName.localized(),
                    description = disclaimer.disclaimer.localized(),
                    modifier = Modifier.weight(1f)
                )
                PermissionDisclaimerActions(
                    onPermit = onRequest,
                    onLater = onDismiss,
                    isVertical = true,
                    modifier = Modifier.width(IntrinsicSize.Max)
                )
            }
        } else {
            Column(modifier = Modifier.padding(BetterNeptunTheme.dimens.paddingLarge)) {
                PermissionDisclaimerContent(
                    title = disclaimer.humanReadablePermissionName.localized(),
                    description = disclaimer.disclaimer.localized()
                )
                Spacer(modifier = Modifier.height(BetterNeptunTheme.dimens.groupSpacing))
                Spacer(Modifier.weight(1f, isSizeMeasured))
                PermissionDisclaimerActions(
                    onPermit = onRequest,
                    onLater = onDismiss,
                    isVertical = false,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun PermissionDisclaimerContent(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = BetterNeptunTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = BetterNeptunTheme.colorScheme.onTertiaryContainer
        )
        Spacer(modifier = Modifier.height(BetterNeptunTheme.dimens.paddingSmall))
        Text(
            text = description,
            style = BetterNeptunTheme.typography.bodyMedium,
            color = BetterNeptunTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun PermissionDisclaimerActions(
    onPermit: () -> Unit,
    onLater: () -> Unit,
    isVertical: Boolean,
    modifier: Modifier = Modifier
) {
    if (isVertical) {
        Column(
            verticalArrangement = Arrangement.spacedBy(BetterNeptunTheme.dimens.paddingSmall),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
        ) {
            PermissionButtons(onPermit, onLater, isVertical = true)
        }
    } else {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(
                BetterNeptunTheme.dimens.paddingSmall,
                Alignment.End
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PermissionButtons(onPermit, onLater, isVertical = false)
        }
    }
}

@Composable
private fun PermissionButtons(
    onPermit: () -> Unit,
    onLater: () -> Unit,
    isVertical: Boolean
) {
    Button(
        onClick = onPermit,
        colors = ButtonDefaults.buttonColors(
            containerColor = BetterNeptunTheme.colorScheme.tertiary,
            contentColor = BetterNeptunTheme.colorScheme.onTertiary
        ),
        shape = BetterNeptunTheme.shapes.medium,
        modifier = if (isVertical) Modifier.fillMaxWidth() else Modifier,
    ) {
        Text(
            text = LocalizationKey.PERMISSION_BUTTON_PERMIT.localized(),
            fontWeight = FontWeight.Bold
        )
    }
    TextButton(
        onClick = onLater,
        colors = ButtonDefaults.textButtonColors(
            contentColor = BetterNeptunTheme.colorScheme.onTertiaryContainer
        ),
        modifier = if (isVertical) Modifier.fillMaxWidth() else Modifier,
    ) {
        Text(
            text = "Later",
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
@OptIn(ExperimentalGridApi::class)
private fun NavigationGrid(
    modifier: Modifier = Modifier,
    columns: Int = 2,
    onNavigate: (NavKey) -> Unit
) {
    val gap = BetterNeptunTheme.dimens.paddingSmall
    Grid(
        config = {
            columns(*Array(columns) { GridTrackSize.Percentage(1f / columns) })
            rows(GridTrackSize.MaxContent)

            gap(gap)
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = BetterNeptunTheme.dimens.itemSpacing)
    ) {
        NavButton(
            icon = painterResource(id = R.drawable.ic_mail),
            text = LocalizationKey.HOME_MENU_MESSAGES.localized(),
            onClick = { onNavigate(MessagesDestination) },
            modifier = Modifier.sharedBoundsAnimation(LocalizationKey.HOME_MENU_MESSAGES)
        )
        NavButton(
            icon = painterResource(id = R.drawable.ic_calendar),
            text = LocalizationKey.HOME_MENU_TIMETABLE.localized(),
            onClick = { onNavigate(TimetableDestination(null)) },
            modifier = Modifier.sharedBoundsAnimation(LocalizationKey.HOME_MENU_TIMETABLE)
        )
        NavButton(
            icon = painterResource(id = R.drawable.ic_courses),
            text = LocalizationKey.HOME_MENU_COURSES.localized(),
            onClick = { onNavigate(SubjectsDestination) },
            modifier = Modifier.sharedBoundsAnimation(LocalizationKey.HOME_MENU_COURSES)
        )
        NavButton(
            icon = painterResource(id = R.drawable.ic_semesters),
            text = LocalizationKey.HOME_MENU_SEMESTERS.localized(),
            onClick = { onNavigate(SemestersDestination) },
            modifier = Modifier.sharedBoundsAnimation(LocalizationKey.HOME_MENU_SEMESTERS)
        )
        NavButton(
            icon = painterResource(id = R.drawable.ic_exams),
            text = LocalizationKey.HOME_MENU_EXAMS.localized(),
            isEnabled = false,
            disabledTag = LocalizationKey.HOME_LABEL_UNDERDEVELOPMENT.localized(),
            onClick = { /* TODO */ }
        )
        NavButton(
            icon = painterResource(id = R.drawable.ic_schedule),
            text = LocalizationKey.HOME_MENU_PERIODS.localized(),
            isEnabled = false,
            disabledTag = LocalizationKey.HOME_LABEL_UNDERDEVELOPMENT.localized(),
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
            horizontalArrangement = Arrangement.spacedBy(BetterNeptunTheme.dimens.extraSmall),
            contentPadding = PaddingValues(horizontal = BetterNeptunTheme.dimens.itemSpacing),
            flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
        ) {
            items(currentCourses) { course ->
                CurrentCourseItem(
                    course = course,
                    onCourseClick = onCourseClick,
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .sharedBoundsAnimation(
                            key = LocalizationKey.HOME_MENU_TIMETABLE.key + course.id.toString()
                        )
                )
            }
        }
    } else {
        EmptyHomeCard(
            text = "Nincs éppen tartó óra",
            modifier = modifier
        )
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
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = BetterNeptunTheme.dimens.itemSpacing)
            .height(intrinsicSize = IntrinsicSize.Max)
    ) {
        Card(
            shape = BetterNeptunTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = BetterNeptunTheme.colorScheme.primaryContainer,
                contentColor = BetterNeptunTheme.colorScheme.onPrimaryContainer
            ),
            modifier = Modifier
                .weight(1f)
                .padding(end = BetterNeptunTheme.dimens.paddingSmall)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(
                        horizontal = BetterNeptunTheme.dimens.paddingLarge,
                        vertical = BetterNeptunTheme.dimens.paddingLarge
                    )
            ) {
                studentData?.avatar?.let {
                    AvatarImage(
                        modifier = Modifier
                            .size(BetterNeptunTheme.dimens.iconHuge)
                            .clip(CircleShape),
                        avatar = studentData.avatar
                    )
                    Spacer(Modifier.width(BetterNeptunTheme.dimens.itemSpacing))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = studentData?.name ?: "",
                        style = BetterNeptunTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = BetterNeptunTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = studentData?.neptun ?: "",
                        style = BetterNeptunTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = BetterNeptunTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
                if (unreadMessages > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = unreadMessages.toString(),
                            style = BetterNeptunTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = BetterNeptunTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(BetterNeptunTheme.dimens.extraSmall))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_mail),
                            contentDescription = null,
                            modifier = Modifier.size(BetterNeptunTheme.dimens.iconSmall),
                            tint = BetterNeptunTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .sharedBoundsAnimation(LocalizationKey.SETTINGS_TITLE),
            shape = BetterNeptunTheme.shapes.large,
            enabled = true,
            onClick = { onNavigateToScreen(SettingsDestination) },
            colors = CardDefaults.cardColors(
                containerColor = BetterNeptunTheme.colorScheme.secondaryContainer,
                contentColor = BetterNeptunTheme.colorScheme.onSecondaryContainer,
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    modifier = Modifier.size(BetterNeptunTheme.dimens.iconExtraLarge),
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
        shape = BetterNeptunTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = BetterNeptunTheme.colorScheme.surfaceVariant,
            contentColor = BetterNeptunTheme.colorScheme.onSurfaceVariant
        ),
        onClick = {
            onCourseClick(course.id)
        }
    ) {
        Column(modifier = Modifier.padding(BetterNeptunTheme.dimens.paddingLarge)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(BetterNeptunTheme.dimens.groupSpacing)
            ) {
                Text(
                    text = LocalizationKey.HOME_ONGOING_COURSE.localized(),
                    style = BetterNeptunTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = BetterNeptunTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                LinearProgressIndicator(
                    progress = { course.progress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(BetterNeptunTheme.dimens.badgeSize),
                    color = Color(course.color),
                    trackColor = Color(course.color).copy(alpha = 0.2f),
                    strokeCap = StrokeCap.Round
                )
            }

            Spacer(modifier = Modifier.height(BetterNeptunTheme.dimens.itemSpacing))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(BetterNeptunTheme.dimens.extraSmall)
                ) {
                    CourseInfoRow(
                        icon = R.drawable.ic_course,
                        text = course.title
                    )
                    if (course.location.isNullOrBlank().not()) {
                        CourseInfoRow(
                            icon = R.drawable.ic_location,
                            text = course.location
                        )
                    }
                }
                CourseInfoRow(
                    icon = R.drawable.ic_schedule,
                    text = LocalizationKey.HOME_ONGOING_COURSE_MINUTES(
                        course.remainingTimeMinutes.toString()
                    ).localized()
                )
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
    if (course != null) {
        Card(
            shape = BetterNeptunTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = BetterNeptunTheme.colorScheme.surfaceVariant,
                contentColor = BetterNeptunTheme.colorScheme.onSurfaceVariant
            ),
            onClick = {
                onCourseClick(course.id)
            },
            modifier = modifier
                .sharedBoundsAnimation(
                    key = LocalizationKey.HOME_MENU_TIMETABLE.key + course.id.toString()
                )
                .fillMaxWidth()
                .padding(horizontal = BetterNeptunTheme.dimens.itemSpacing)
        ) {
            Box(
                modifier = Modifier
                    .padding(
                        horizontal = BetterNeptunTheme.dimens.paddingLarge,
                        vertical = BetterNeptunTheme.dimens.paddingLarge
                    )
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.height(intrinsicSize = IntrinsicSize.Max),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(BetterNeptunTheme.dimens.extraSmall)
                    ) {
                        Text(
                            text = LocalizationKey.HOME_NEXT_COURSE.localized(),
                            style = BetterNeptunTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BetterNeptunTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(BetterNeptunTheme.dimens.extraSmall))
                        CourseInfoRow(
                            icon = R.drawable.ic_event,
                            text = when (course.timeUntilEvent.unit) {
                                TimeDuration.Unit.MINUTES -> LocalizationKey.HOME_NEXT_COURSE_MINUTES(
                                    course.timeUntilEvent.value.toString()
                                ).localized()

                                TimeDuration.Unit.HOURS -> LocalizationKey.HOME_NEXT_COURSE_HOURS(
                                    course.timeUntilEvent.value.toString()
                                ).localized()

                                TimeDuration.Unit.DAYS -> LocalizationKey.HOME_NEXT_COURSE_DAYS(
                                    course.timeUntilEvent.value.toString()
                                ).localized()
                            }
                        )
                        CourseInfoRow(
                            icon = R.drawable.ic_course,
                            text = course.title
                        )
                        if (course.location.isNullOrBlank().not()) {
                            CourseInfoRow(
                                icon = R.drawable.ic_location,
                                text = course.location
                            )
                        }
                    }
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(vertical = BetterNeptunTheme.dimens.itemSpacing)
                    ) {
                        Text(
                            text = "${course.startTime.hour}:${
                                course.startTime.minute.toString().padStart(2, '0')
                            }",
                            style = BetterNeptunTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = BetterNeptunTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${course.endTime.hour}:${
                                course.endTime.minute.toString().padStart(2, '0')
                            }",
                            style = BetterNeptunTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = BetterNeptunTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(BetterNeptunTheme.dimens.itemSpacing))
                    Box(
                        modifier = Modifier
                            .width(BetterNeptunTheme.dimens.small)
                            .fillMaxHeight()
                            .padding(vertical = BetterNeptunTheme.dimens.itemSpacing)
                            .background(Color(course.color), CircleShape)
                    )
                }
            }
        }
    } else {
        EmptyHomeCard(
            text = "Nincs több óra mára",
            modifier = modifier
        )
    }
}

@Composable
private fun EmptyHomeCard(
    text: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = BetterNeptunTheme.dimens.itemSpacing),
        shape = BetterNeptunTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = BetterNeptunTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            contentColor = BetterNeptunTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Box(
            modifier = Modifier
                .padding(BetterNeptunTheme.dimens.paddingLarge)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = BetterNeptunTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
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
        shape = BetterNeptunTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = BetterNeptunTheme.colorScheme.secondaryContainer,
            contentColor = BetterNeptunTheme.colorScheme.onSecondaryContainer,
            disabledContentColor = BetterNeptunTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
            disabledContainerColor = BetterNeptunTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(BetterNeptunTheme.dimens.paddingLarge),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(BetterNeptunTheme.dimens.iconExtraLarge)
            )
            Spacer(modifier = Modifier.height(BetterNeptunTheme.dimens.paddingSmall))
            Text(
                text = text,
                style = BetterNeptunTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
            )
            disabledTag?.let {
                Text(
                    text = it,
                    style = BetterNeptunTheme.typography.labelSmall,
                    color = BetterNeptunTheme.colorScheme.onSecondaryContainer.copy(
                        alpha = 0.5f
                    )
                )
            }
        }
    }
}

@Composable
fun CourseInfoRow(
    @DrawableRes icon: Int,
    text: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(BetterNeptunTheme.dimens.iconSmall),
            tint = BetterNeptunTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.width(BetterNeptunTheme.dimens.paddingSmall))
        Text(
            text = text.trim(),
            style = BetterNeptunTheme.typography.bodyMedium,
            color = BetterNeptunTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun HomeScreenPreviewContent() {
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
        startTime = LocalDateTime.now().plus(1.hours),
        endTime = LocalDateTime.now().plus(1.hours),
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
        permissions = emptyList(),
        refreshProgress = null,
        onRefresh = {},
        onLaunchPermissionRequest = {},
        onDismissPermissionRequest = {},
        onNavigate = {}
    )
}

@Preview(
    name = "Phone - Landscape",
    device = "spec:width=411dp,height=891dp,orientation=landscape,dpi=420",
)
@PreviewWrapper(PreviewThemeProvider::class)
@Composable
fun HomeScreenLandscapePreview() {
    HomeScreenPreviewContent()
}

@Preview
@PreviewWrapper(PreviewThemeProvider::class)
@Composable
fun HomeScreenPreview() {
    HomeScreenPreviewContent()
}
