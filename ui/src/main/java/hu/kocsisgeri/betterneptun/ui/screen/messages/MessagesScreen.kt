package hu.kocsisgeri.betterneptun.ui.screen.messages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import hu.kocsisgeri.betterneptun.common.utils.formatApiDate
import hu.kocsisgeri.betterneptun.domain.model.neptun.Avatar
import hu.kocsisgeri.betterneptun.domain.model.neptun.Message
import hu.kocsisgeri.betterneptun.domain.model.neptun.MessagesPager
import hu.kocsisgeri.betterneptun.localization.LocalizationKey
import hu.kocsisgeri.betterneptun.ui.R
import hu.kocsisgeri.betterneptun.ui.core.composable.AvatarImage
import hu.kocsisgeri.betterneptun.ui.core.composable.ScrollBar
import hu.kocsisgeri.betterneptun.ui.core.modifier.sharedBoundsAnimation
import hu.kocsisgeri.betterneptun.ui.core.theme.Armata
import hu.kocsisgeri.betterneptun.ui.core.theme.BetterNeptunTheme
import hu.kocsisgeri.betterneptun.ui.core.theme.PreviewThemeProvider
import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
import hu.kocsisgeri.betterneptun.ui.navigation.destination.MessageDetailDestination
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MessagesScreen(
    viewModel: MessagesViewModel = koinViewModel(),
    navigator: Navigator = koinInject()
) {
    val messages by viewModel.listItems.collectAsStateWithLifecycle(
        minActiveState = Lifecycle.State.RESUMED
    )

    MessagesContent(
        messages = messages,
        onBackClick = navigator::navigateBack,
        onRetryClick = viewModel::refresh,
        onLoadMore = viewModel::loadMore,
        onMessageClick = { message ->
            navigator.navigateTo(MessageDetailDestination(message.id))
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesContent(
    messages: MessagesPager,
    onRetryClick: () -> Unit,
    onBackClick: () -> Unit,
    onLoadMore: () -> Unit,
    onMessageClick: (Message) -> Unit
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
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Üzenetek",
                        fontFamily = Armata,
                        fontWeight = FontWeight.Bold,
                        style = BetterNeptunTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Vissza"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BetterNeptunTheme.colorScheme.background,
                    scrolledContainerColor = BetterNeptunTheme.colorScheme.background,
                    navigationIconContentColor = BetterNeptunTheme.colorScheme.onBackground,
                    titleContentColor = BetterNeptunTheme.colorScheme.onBackground,
                )
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
                .padding(paddingValues)
                .padding(horizontal = BetterNeptunTheme.dimens.paddingSmall)
                .clip(BetterNeptunTheme.shapes.large)
        ) {
            messages(messages, onMessageClick)
            errorMessage(messages, onRetryClick)
            loadingMessage(messages)
            endMessage(messages)
        }

        ScrollBar(
            modifier = Modifier.padding(paddingValues),
            state = listState
        )
    }
}

private fun LazyListScope.messages(
    messages: MessagesPager,
    onMessageClick: (Message) -> Unit
) {
    items(
        items = messages.messages,
        key = { message -> message.id }
    ) {  message ->
        MessageItem(
            modifier = Modifier.animateItem(),
            message = message,
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
    if (messages.isLoadingNextMessages) {
        item(key = "LOADING_ITEM") {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .then(
                        if (messages.messages.isEmpty()) Modifier.fillParentMaxHeight()
                        else Modifier
                    )
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                CircularProgressIndicator(
                    color = BetterNeptunTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Üzenetek betöltése...",
                    style = BetterNeptunTheme.typography.bodyMedium,
                    color = BetterNeptunTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun LazyListScope.errorMessage(
    messages: MessagesPager,
    onRetryClick: () -> Unit
) {
    messages.error?.let {
        item(key = "ERROR_MESSAGE") {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(BetterNeptunTheme.dimens.medium),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Warning,
                    contentDescription = null,
                    modifier = Modifier.size(BetterNeptunTheme.dimens.iconGiant),
                    tint = BetterNeptunTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(BetterNeptunTheme.dimens.medium))
                Text(
                    text = "Hiba történt az üzenetek betőltése közben.",
                    style = BetterNeptunTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = BetterNeptunTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(BetterNeptunTheme.dimens.large))
                Button(
                    onClick = onRetryClick,
                    shape = BetterNeptunTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BetterNeptunTheme.colorScheme.primary,
                        contentColor = BetterNeptunTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Újra")
                }
            }
        }
    }
}

@Composable
fun MessageItem(
    message: Message,
    modifier: Modifier = Modifier,
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
            containerColor = BetterNeptunTheme.colorScheme.primaryContainer
        )
    )
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
        onRetryClick = {},
        onBackClick = {},
        onLoadMore = {},
        onMessageClick = {}
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
        onRetryClick = {},
        onBackClick = {},
        onLoadMore = {},
        onMessageClick = {}
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
        onRetryClick = {},
        onBackClick = {},
        onLoadMore = {},
        onMessageClick = {}
    )
}
