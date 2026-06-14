package hu.kocsisgeri.betterneptun.ui.screen.messages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import hu.kocsisgeri.betterneptun.common.utils.formatApiDate
import hu.kocsisgeri.betterneptun.domain.model.neptun.Avatar
import hu.kocsisgeri.betterneptun.domain.model.neptun.Message
import hu.kocsisgeri.betterneptun.domain.model.neptun.MessagesPager
import hu.kocsisgeri.betterneptun.localization.LocalizationKey
import hu.kocsisgeri.betterneptun.ui.designsystem.R
import hu.kocsisgeri.betterneptun.ui.designsystem.composable.AvatarImage
import hu.kocsisgeri.betterneptun.ui.designsystem.composable.ScrollBar
import hu.kocsisgeri.betterneptun.ui.designsystem.composable.rememberShimmerProgress
import hu.kocsisgeri.betterneptun.ui.designsystem.composable.sharedShimmer
import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
import hu.kocsisgeri.betterneptun.ui.navigation.destination.MessageDetailDestination
import hu.kocsisgeri.betterneptun.ui.navigation.modifier.isLandscape
import hu.kocsisgeri.betterneptun.ui.navigation.modifier.sharedBoundsAnimation
import hu.kocsisgeri.betterneptun.ui.screen.messages.detail.MessageDetailContent
import hu.kocsisgeri.betterneptun.ui.screen.messages.detail.MessageDetailViewModel
import hu.kocsisgeri.betterneptun.ui.theme.Armata
import hu.kocsisgeri.betterneptun.ui.theme.BetterNeptunTheme
import hu.kocsisgeri.betterneptun.ui.theme.PreviewThemeProvider
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun MessagesScreen(
    viewModel: MessagesViewModel = koinViewModel(),
    navigator: Navigator = koinInject()
) {
    val messages by viewModel.listItems.collectAsStateWithLifecycle()
    val selectedMessageId by viewModel.selectedMessageId.collectAsStateWithLifecycle()

    if (isLandscape()) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(BetterNeptunTheme.colorScheme.background)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                MessagesContent(
                    messages = messages,
                    selectedMessageId = selectedMessageId,
                    onBackClick = navigator::navigateBack,
                    onLoadMore = viewModel::loadMore,
                    onMessageClick = { message ->
                        viewModel.selectMessage(message.id)
                    }
                )
            }
            Box(modifier = Modifier.weight(1.5f)) {
                selectedMessageId?.let { messageId ->
                    MessageDetailPane(messageId = messageId)
                } ?: NoSelectedMessageContent()
            }
        }
    } else {
        MessagesContent(
            messages = messages,
            onBackClick = navigator::navigateBack,
            onLoadMore = viewModel::loadMore,
            onMessageClick = { message ->
                navigator.navigateTo(MessageDetailDestination(message.id))
            },
        )
    }
}

@Composable
private fun NoSelectedMessageContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.List,
                contentDescription = null,
                modifier = Modifier.size(BetterNeptunTheme.dimens.iconGiant),
                tint = BetterNeptunTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(Modifier.height(BetterNeptunTheme.dimens.medium))
            Text(
                text = "Válassz ki egy üzenetet\na megtekintéshez",
                style = BetterNeptunTheme.typography.bodyLarge,
                color = BetterNeptunTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun MessageDetailPane(messageId: String) {
    val viewModel: MessageDetailViewModel = koinViewModel(
        key = messageId,
        parameters = { parametersOf(messageId) }
    )
    val message by viewModel.message.collectAsStateWithLifecycle()

    MessageDetailContent(
        message = message,
        showTopBar = false,
        onBackClick = null,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesContent(
    messages: MessagesPager,
    onBackClick: () -> Unit,
    onLoadMore: () -> Unit,
    onMessageClick: (Message) -> Unit,
    selectedMessageId: String? = null,
) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val shouldLoadMore by remember {
        snapshotFlow { listState.layoutInfo }.map { info ->
            val lastVisibleItemIndex = info.visibleItemsInfo.lastOrNull()?.index ?: -1
            val totalItemsCount = info.totalItemsCount
            lastVisibleItemIndex >= totalItemsCount - 5 && totalItemsCount > 0
        }
    }.collectAsStateWithLifecycle(
        initialValue = false,
        minActiveState = Lifecycle.State.RESUMED
    )

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) onLoadMore()
    }

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .sharedBoundsAnimation(LocalizationKey.HOME_MENU_MESSAGES),
        topBar = {
            if (isLandscape()) TopAppBar(
                title = { TopBarTitle() },
                navigationIcon = { TopBarNavigationIcon(onBackClick) },
                colors = topBarDefaultColors()
            ) else LargeTopAppBar(
                title = { TopBarTitle() },
                navigationIcon = { TopBarNavigationIcon(onBackClick) },
                scrollBehavior = scrollBehavior,
                colors = topBarDefaultColors()
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = listState.canScrollBackward,
                enter = slideInHorizontally { it } + fadeIn(),
                exit = fadeOut() + slideOutHorizontally { it },
            ) {
                FloatingActionButton(
                    onClick = { scope.launch { listState.animateScrollToItem(0) } },
                    containerColor = BetterNeptunTheme.colorScheme.primary,
                    contentColor = BetterNeptunTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowUpward,
                        contentDescription = null
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(BetterNeptunTheme.dimens.extraSmall / 2),
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (isLandscape()) {
                        Modifier.padding(
                            end = paddingValues.calculateStartPadding(
                                LocalLayoutDirection.current
                            ),
                            start = BetterNeptunTheme.dimens.extraSmall,
                            top = paddingValues.calculateTopPadding(),
                            bottom = paddingValues.calculateBottomPadding()
                        )
                    } else {
                        Modifier.padding(paddingValues)
                    }
                )
                .padding(horizontal = BetterNeptunTheme.dimens.paddingSmall)
                .clip(BetterNeptunTheme.shapes.large)
        ) {
            messages(messages, selectedMessageId, onMessageClick)
            loadingMessage(messages)
            endMessage(messages)
        }

        ScrollBar(
            modifier = Modifier.padding(paddingValues),
            state = listState
        )
    }
}

@Composable
private fun topBarDefaultColors(): TopAppBarColors = TopAppBarDefaults.topAppBarColors(
    containerColor = BetterNeptunTheme.colorScheme.background,
    scrolledContainerColor = BetterNeptunTheme.colorScheme.background,
    navigationIconContentColor = BetterNeptunTheme.colorScheme.onBackground,
    titleContentColor = BetterNeptunTheme.colorScheme.onBackground,
)

@Composable
private fun TopBarNavigationIcon(onBackClick: () -> Unit) {
    IconButton(onClick = onBackClick) {
        Icon(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = "Vissza"
        )
    }
}

@Composable
private fun TopBarTitle() {
    Text(
        text = "Üzenetek",
        fontFamily = Armata,
        fontWeight = FontWeight.Bold,
        style = BetterNeptunTheme.typography.titleLarge
    )
}

@Composable
fun MessageItem(
    message: Message,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    ListItem(
        modifier = modifier
            .clip(BetterNeptunTheme.shapes.small)
            .clickable(onClick = onClick),
        headlineContent = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = message.name,
                fontFamily = Armata,
                fontWeight = if (message.isNew) FontWeight.ExtraBold else FontWeight.Medium,
                style = BetterNeptunTheme.typography.bodyLarge,
                color = if (message.isNew) BetterNeptunTheme.colorScheme.primary
                else BetterNeptunTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        supportingContent = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = message.subject,
                style = BetterNeptunTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = BetterNeptunTheme.colorScheme.onSurfaceVariant,
            )
        },
        leadingContent = {
            AvatarImage(
                modifier = Modifier
                    .size(BetterNeptunTheme.dimens.iconHuge)
                    .clip(CircleShape),
                avatar = message.senderAvatar
            )
        },
        trailingContent = {
            Box(
                modifier = Modifier.height(BetterNeptunTheme.dimens.extraLarge),
                contentAlignment = Alignment.TopEnd
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(BetterNeptunTheme.dimens.small)
                ) {
                    Text(
                        text = message.date.formatApiDate(),
                        style = BetterNeptunTheme.typography.labelSmall.copy(
                            fontWeight = if (message.isNew) {
                                FontWeight.ExtraBold
                            } else {
                                FontWeight.Normal
                            }
                        ),
                        color = BetterNeptunTheme.colorScheme.onSurfaceVariant
                    )
                    if (message.isNew) {
                        Badge(
                            containerColor = BetterNeptunTheme.colorScheme.primary,
                            modifier = Modifier.size(BetterNeptunTheme.dimens.badgeSize)
                        )
                    }
                }
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = if (isSelected) BetterNeptunTheme.colorScheme.secondaryContainer
            else BetterNeptunTheme.colorScheme.primaryContainer
        )
    )
}

@Composable
fun MessageItemLoading(
    modifier: Modifier = Modifier,
    progress: State<Float>,
) {
    val shimmerProgress by progress

    ListItem(
        modifier = modifier.clip(BetterNeptunTheme.shapes.small),
        headlineContent = {
            Text(
                text = "",
                fontFamily = Armata,
                style = BetterNeptunTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth(.5f)
                    .padding(bottom = BetterNeptunTheme.dimens.small / 2)
                    .sharedShimmer(shimmerProgress),
            )
        },
        supportingContent = {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .sharedShimmer(shimmerProgress),
                text = "",
                style = BetterNeptunTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = BetterNeptunTheme.colorScheme.onSurfaceVariant,
            )
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(BetterNeptunTheme.dimens.iconHuge)
                    .clip(CircleShape)
                    .sharedShimmer(shimmerProgress),
            )
        },
        trailingContent = {
            Box(
                modifier = Modifier.height(BetterNeptunTheme.dimens.extraLarge),
                contentAlignment = Alignment.TopEnd
            ) {
                Text(
                    text = List(30) { " " }.joinToString(""),
                    style = BetterNeptunTheme.typography.labelSmall,
                    color = BetterNeptunTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.sharedShimmer(shimmerProgress)
                )
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = BetterNeptunTheme.colorScheme.primaryContainer
        )
    )
}

private fun LazyListScope.messages(
    messages: MessagesPager,
    selectedMessageId: String?,
    onMessageClick: (Message) -> Unit
) {
    items(
        items = messages.messages,
        key = { message -> message.id }
    ) { message ->
        MessageItem(
            modifier = Modifier.animateItem(),
            message = message,
            isSelected = message.id == selectedMessageId,
            onClick = { onMessageClick(message) }
        )
    }
}

private fun LazyListScope.endMessage(messages: MessagesPager) {
    if (messages.isEndReached) {
        item(key = "END_ITEM") {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(BetterNeptunTheme.dimens.medium),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.List,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(BetterNeptunTheme.dimens.medium))
                Text(
                    text = "A lista végére értél, nem lehet több üzenet betölteni.",
                    style = BetterNeptunTheme.typography.bodyMedium,
                    color = BetterNeptunTheme.colorScheme.onSurfaceVariant
                )
            }
        }

    }
}

private fun LazyListScope.loadingMessage(messages: MessagesPager) {
    if (messages.isLoadingNextMessages || messages.error != null) {
        item(key = "LOADING_ITEM") {
            val shimmerProgress = rememberShimmerProgress()

            repeat(if (messages.messages.isEmpty()) 10 else 1) {
                MessageItemLoading(
                    modifier = Modifier,
                    progress = shimmerProgress
                )
                Spacer(Modifier.height(BetterNeptunTheme.dimens.extraSmall / 2))
            }
        }
    }
}

@PreviewLightDark
@PreviewWrapper(PreviewThemeProvider::class)
@Composable
fun MessagesSuccessPreview() {
    MessagesContent(
        messages = MessagesPager(
            messages = listOf(
                Message(
                    id = "1",
                    name = "Kovács János",
                    subject = "Vizsga eredmény",
                    date = LocalDateTime(2023, 10, 25, 14, 30),
                    isNew = true,
                    senderAvatar = Avatar.MonogramAvatar(
                        monogram = "KJ",
                        colorLong = 0xFF000000
                    )
                ),
                Message(
                    id = "2",
                    name = "Neptun Rendszer",
                    subject = "Kurzusfelvétel",
                    date = LocalDateTime(2023, 10, 24, 9, 15),
                    isNew = false,
                    senderAvatar = Avatar.SystemAvatar
                ),
                Message(
                    id = "3",
                    name = "Kósa Kálmán",
                    subject = "Elmaradt előadás",
                    date = LocalDateTime(2023, 10, 23, 18, 0),
                    isNew = true,
                    senderAvatar = Avatar.MonogramAvatar(
                        monogram = "KK",
                        colorLong = 0xFFFFFFFF
                    )
                )
            ),
            isLoadingNextMessages = false
        ),
        onBackClick = {},
        onLoadMore = {},
        onMessageClick = {},
    )
}

@PreviewLightDark
@PreviewWrapper(PreviewThemeProvider::class)
@Composable
fun MessagesLoadingPreview() {
    MessagesContent(
        messages = MessagesPager(
            isLoadingNextMessages = true
        ),
        onBackClick = {},
        onLoadMore = {},
        onMessageClick = {},
    )
}

@PreviewLightDark
@PreviewWrapper(PreviewThemeProvider::class)
@Composable
fun MessagesErrorPreview() {
    MessagesContent(
        messages = MessagesPager(
            error = "Hiba történt az üzenetek betöltése közben.",
            isLoadingNextMessages = false
        ),
        onBackClick = {},
        onLoadMore = {},
        onMessageClick = {},
    )
}
