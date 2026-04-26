package hu.kocsisgeri.betterneptun.ui.screen.messages.detail_dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import hu.kocsisgeri.betterneptun.R
import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
import hu.kocsisgeri.betterneptun.ui.screen.messages.MessagesViewModel
import hu.kocsisgeri.betterneptun.utils.HtmlText
import hu.kocsisgeri.betterneptun.utils.openUrl
import hu.kocsisgeri.betterneptun.utils.sendEmail
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageDetailScreen(
    viewModel: MessagesViewModel = koinViewModel(),
    navigator: Navigator,
) {
    val messageDetail by viewModel.messageDetail.observeAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
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
                    IconButton(onClick = { navigator.navigateBack() }) {
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
            messageDetail?.let { message ->
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
                                text = message.subject,
                                style = MaterialTheme.typography.headlineSmall,
                                fontFamily = hu.kocsisgeri.betterneptun.ui.theme.Armata,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            
                            DetailItem(
                                icon = painterResource(R.drawable.ic_mail),
                                label = "Küldő",
                                value = message.sender
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            DetailItem(
                                icon = painterResource(R.drawable.ic_schedule),
                                label = "Küldés ideje",
                                value = message.date
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
                        val rawHtml = message.posts.firstOrNull()?.htmlText ?: ""
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
                            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily ?: hu.kocsisgeri.betterneptun.ui.theme.Armata,
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
            } ?: Box(
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
