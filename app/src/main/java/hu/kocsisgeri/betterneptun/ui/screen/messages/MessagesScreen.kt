package hu.kocsisgeri.betterneptun.ui.screen.messages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Badge
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hu.kocsisgeri.betterneptun.R
import hu.kocsisgeri.betterneptun.domain.model.Message
import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
import hu.kocsisgeri.betterneptun.ui.navigation.destination.MessageDetailDestination
import hu.kocsisgeri.betterneptun.ui.theme.Armata
import hu.kocsisgeri.betterneptun.ui.theme.BetterNeptunTheme
import hu.kocsisgeri.betterneptun.common.DateUtils
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import java.time.LocalDateTime

@Composable
fun MessagesScreen(
    viewModel: MessagesViewModel = koinViewModel(),
    navigator: Navigator = koinInject()
) {
    val messages by viewModel.listItems.observeAsState(emptyList())
    
    MessagesContent(
        messages = messages,
        onBackClick = navigator::navigateBack,
        onMessageClick = { message ->
            navigator.navigateTo(MessageDetailDestination(message.id))
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesContent(
    messages: List<Message>,
    onBackClick: () -> Unit,
    onMessageClick: (Message) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
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
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (messages.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
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
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(messages) { message ->
                        MessageItem(
                            message = message,
                            onClick = { onMessageClick(message) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MessageItem(
    message: Message,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = {
            Text(
                text = message.name,
                fontFamily = Armata,
                fontWeight = if (message.isNew) FontWeight.Bold else FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyLarge,
                color = if (message.isNew) MaterialTheme.colorScheme.primary 
                        else MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        supportingContent = {
            Text(
                text = message.subject,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingContent = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = DateUtils.formatDate(message.date),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (message.isNew) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Badge(
                        containerColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(8.dp)
                    )
                }
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = if (message.isNew) 
                MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surface
        )
    )
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outlineVariant
    )
}

@Preview(showBackground = true)
@Composable
fun MessagesScreenPreview() {
    BetterNeptunTheme {
        MessagesContent(
            messages = listOf(
                Message(
                    id = "1",
                    name = "Kovács János",
                    subject = "Vizsga eredmény",
                    date = LocalDateTime.of(2023, 10, 25, 14, 30),
                    isNew = true,
                    detail = "Tisztelt Hallgató! A vizsgája sikerült."
                ),
                Message(
                    id = "2",
                    name = "Neptun Rendszer",
                    subject = "Kurzusfelvétel",
                    date = LocalDateTime.of(2023, 10, 24, 9, 15),
                    isNew = false,
                    detail = "A kurzusfelvétel időszaka megkezdődött."
                ),
                Message(
                    id = "3",
                    name = "Dr. Tanár Úr",
                    subject = "Elmaradt előadás",
                    date = LocalDateTime.of(2023, 10, 23, 18, 0),
                    isNew = true,
                    detail = "A holnapi előadás betegség miatt elmarad."
                )
            ),
            onBackClick = {},
            onMessageClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MessageItemPreview() {
    BetterNeptunTheme {
        Column {
            MessageItem(
                message = Message(
                    id = "1",
                    name = "Kovács János",
                    subject = "Vizsga eredmény",
                    date = LocalDateTime.of(2023, 10, 25, 14, 30),
                    isNew = true,
                    detail = "Tisztelt Hallgató! A vizsgája sikerült."
                ),
                onClick = {}
            )
            MessageItem(
                message = Message(
                    id = "2",
                    name = "Neptun Rendszer",
                    subject = "Kurzusfelvétel",
                    date = LocalDateTime.of(2023, 10, 24, 9, 15),
                    isNew = false,
                    detail = "A kurzusfelvétel időszaka megkezdődött."
                ),
                onClick = {}
            )
        }
    }
}
