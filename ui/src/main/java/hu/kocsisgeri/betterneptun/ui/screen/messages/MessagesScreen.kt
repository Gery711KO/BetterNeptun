package hu.kocsisgeri.betterneptun.ui.screen.messages

import android.content.res.Configuration
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
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import hu.kocsisgeri.betterneptun.common.DateUtils
import hu.kocsisgeri.betterneptun.domain.model.Avatar
import hu.kocsisgeri.betterneptun.domain.model.Message
import hu.kocsisgeri.betterneptun.domain.model.MessagesPager
import hu.kocsisgeri.betterneptun.ui.R
import hu.kocsisgeri.betterneptun.ui.composable.AvatarImage
import hu.kocsisgeri.betterneptun.ui.composable.ScrollBar
import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
import hu.kocsisgeri.betterneptun.ui.navigation.destination.MessageDetailDestination
import hu.kocsisgeri.betterneptun.ui.theme.Armata
import hu.kocsisgeri.betterneptun.ui.theme.BetterNeptunTheme
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import java.time.LocalDateTime

@Composable
fun MessagesScreen(
    viewModel: MessagesViewModel = koinViewModel(),
    navigator: Navigator = koinInject()
) {
    val messages by viewModel.listItems.collectAsStateWithLifecycle()

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
        derivedStateOf {
            val lastVisibleItemIndex =
                listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            val totalItemsCount = listState.layoutInfo.totalItemsCount
            lastVisibleItemIndex >= totalItemsCount - 5 && totalItemsCount > 0
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            onLoadMore()
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Üzenetek",
                        fontFamily = Armata,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
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
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
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
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
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
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 8.dp)
                .clip(MaterialTheme.shapes.large)
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
    itemsIndexed(
        items = messages.messages,
        key = { _, message -> message.id }
    ) { index, message ->
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
                    .padding(16.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.List,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "A lista végére értél, nem lehet több üzenet betölteni.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Üzenetek betöltése...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    .padding(16.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Warning,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Hiba történt az üzenetek betőltése közben.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onRetryClick,
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
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
            .clip(MaterialTheme.shapes.small)
            .clickable(onClick = onClick),
        headlineContent = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = message.name,
                fontFamily = Armata,
                fontWeight = if (message.isNew) FontWeight.ExtraBold else FontWeight.Medium,
                style = MaterialTheme.typography.bodyLarge,
                color = if (message.isNew) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        supportingContent = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = message.subject,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        leadingContent = {
            AvatarImage(
                modifier = Modifier.size(48.dp).clip(CircleShape),
                avatar = message.senderAvatar
            )
        },
        trailingContent = {
            Box(
                modifier = Modifier.height(32.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = DateUtils.formatDate(message.date),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (message.isNew) {
                                FontWeight.ExtraBold
                            } else {
                                FontWeight.Normal
                            }
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (message.isNew) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(9.dp)
                        )
                    }
                }
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    )
}

@Preview(showBackground = true, name = "Success - Light")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Success - Dark")
@Composable
fun MessagesSuccessPreview() {
    BetterNeptunTheme {
        MessagesContent(
            messages = MessagesPager(
                messages = listOf(
                    Message(
                        id = "1",
                        name = "Kovács János",
                        subject = "Vizsga eredmény",
                        date = LocalDateTime.of(2023, 10, 25, 14, 30),
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
                        date = LocalDateTime.of(2023, 10, 24, 9, 15),
                        isNew = false,
                        senderAvatar = Avatar.SystemAvatar
                    ),
                    Message(
                        id = "3",
                        name = "Kósa Kálmán",
                        subject = "Elmaradt előadás",
                        date = LocalDateTime.of(2023, 10, 23, 18, 0),
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
}

@Preview(showBackground = true, name = "Loading - Light")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Loading - Dark")
@Composable
fun MessagesLoadingPreview() {
    BetterNeptunTheme {
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
}

@Preview(showBackground = true, name = "Error - Light")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Error - Dark")
@Composable
fun MessagesErrorPreview() {
    BetterNeptunTheme {
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
}
