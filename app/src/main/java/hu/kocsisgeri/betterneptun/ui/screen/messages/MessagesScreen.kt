package hu.kocsisgeri.betterneptun.ui.screen.messages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import hu.kocsisgeri.betterneptun.R
import hu.kocsisgeri.betterneptun.domain.model.Message
import hu.kocsisgeri.betterneptun.ui.theme.Armata
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    viewModel: MessagesViewModel = koinViewModel(),
    onBackClick: () -> Unit,
    onMessageClick: (String) -> Unit
) {
    val messages by viewModel.listItems.observeAsState(emptyList())
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
                scrollBehavior = scrollBehavior
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
                            onClick = {
                                viewModel.readMessage(message.id)
                                onMessageClick(message.id)
                            }
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
                    text = message.date,
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
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f) 
                else MaterialTheme.colorScheme.surface
        )
    )
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outlineVariant
    )
}
