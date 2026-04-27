package hu.kocsisgeri.betterneptun.ui.screen.messages.detail

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import hu.kocsisgeri.betterneptun.R
import hu.kocsisgeri.betterneptun.domain.model.MessageDetail
import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
import hu.kocsisgeri.betterneptun.ui.theme.BetterNeptunTheme
import hu.kocsisgeri.betterneptun.utils.DateUtils
import hu.kocsisgeri.betterneptun.utils.HtmlText
import hu.kocsisgeri.betterneptun.utils.openUrl
import hu.kocsisgeri.betterneptun.utils.sendEmail
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import java.time.LocalDateTime

@Composable
fun MessageDetailScreen(
    messageId: String,
    viewModel: MessageDetailViewModel = koinViewModel { parametersOf(messageId) },
    navigator: Navigator = koinInject(),
) {
    val messageDetail by viewModel.messageDetail.collectAsStateWithLifecycle()
    val isError by viewModel.isError.collectAsStateWithLifecycle()

    MessageDetailContent(
        messageDetail = messageDetail,
        isError = isError,
        onBackClick = { navigator.navigateBack() },
        onRetryClick = { viewModel.refresh() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageDetailContent(
    messageDetail: MessageDetail?,
    isError: String?,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (isError == null && messageDetail != null) {
                MediumTopAppBar(
                    title = {
                        Text(
                            text = "Üzenet részletei",
                            style = MaterialTheme.typography.titleLarge,
                            fontFamily = hu.kocsisgeri.betterneptun.ui.theme.Armata,
                            fontWeight = FontWeight.Bold
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
            } else {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_back),
                                contentDescription = "Vissza"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                        navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                    )
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isError != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = isError,
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

                messageDetail != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp)
                    ) {
                        // Message Header
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            shape = MaterialTheme.shapes.extraLarge,
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = messageDetail.subject,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontFamily = hu.kocsisgeri.betterneptun.ui.theme.Armata,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(20.dp))

                                DetailItem(
                                    icon = painterResource(R.drawable.ic_mail),
                                    label = "Küldő",
                                    value = messageDetail.sender
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                DetailItem(
                                    icon = painterResource(R.drawable.ic_schedule),
                                    label = "Küldés ideje",
                                    value = DateUtils.formatDate(messageDetail.date)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Message Content
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.extraLarge,
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        ) {
                            val rawHtml = messageDetail.posts.firstOrNull()?.htmlText ?: ""
                            val processedHtml = if (rawHtml.contains("}")) {
                                rawHtml.split("}").last()
                            } else {
                                rawHtml
                            }

                            HtmlText(
                                html = processedHtml,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                                    ?: hu.kocsisgeri.betterneptun.ui.theme.Armata,
                                onUrlClick = { url ->
                                    when {
                                        url.contains("http") -> openUrl(url, context)
                                        url.contains("mailto:") || url.contains("@") -> {
                                            url.removePrefix("mailto:").trim().sendEmail(context)
                                        }
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }

                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailItem(icon: Painter, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = hu.kocsisgeri.betterneptun.ui.theme.Armata,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MessageDetailContentPreview() {
    BetterNeptunTheme {
        MessageDetailContent(
            messageDetail = MessageDetail(
                subject = "Vizsga eredmény",
                sender = "Kovács János",
                date = LocalDateTime.of(2023,10,25,14,30),
                posts = listOf(
                    MessageDetail.Post(
                        id = "1",
                        htmlText = "Tisztelt Hallgató! <br><br> A vizsgája <b>sikerült</b>. <br><br> Üdvözlettel, <br> Tanár Úr"
                    )
                )
            ),
            isError = null,
            onBackClick = {},
            onRetryClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MessageDetailErrorPreview() {
    BetterNeptunTheme {
        MessageDetailContent(
            messageDetail = null,
            isError = "Nincs internetkapcsolat!",
            onBackClick = {},
            onRetryClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MessageDetailLoadingPreview() {
    BetterNeptunTheme {
        MessageDetailContent(
            messageDetail = null,
            isError = null,
            onBackClick = {},
            onRetryClick = {}
        )
    }
}
