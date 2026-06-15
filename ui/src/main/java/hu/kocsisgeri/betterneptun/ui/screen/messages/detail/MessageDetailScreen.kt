package hu.kocsisgeri.betterneptun.ui.screen.messages.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import hu.kocsisgeri.betterneptun.common.utils.now
import hu.kocsisgeri.betterneptun.domain.model.neptun.Avatar
import hu.kocsisgeri.betterneptun.domain.model.neptun.Message
import hu.kocsisgeri.betterneptun.domain.model.neptun.MessageDetail
import hu.kocsisgeri.betterneptun.ui.designsystem.R
import hu.kocsisgeri.betterneptun.ui.designsystem.composable.AvatarImage
import hu.kocsisgeri.betterneptun.ui.designsystem.composable.HtmlText
import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
import hu.kocsisgeri.betterneptun.ui.navigation.modifier.isLandscape
import hu.kocsisgeri.betterneptun.ui.theme.Armata
import hu.kocsisgeri.betterneptun.ui.theme.BetterNeptunTheme
import hu.kocsisgeri.betterneptun.ui.theme.PreviewThemeProvider
import kotlinx.datetime.LocalDateTime
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun MessageDetailScreen(
    messageId: String,
    viewModel: MessageDetailViewModel = koinViewModel { parametersOf(messageId) },
    navigator: Navigator = koinInject(),
) {
    val message by viewModel.message.collectAsStateWithLifecycle()

    MessageDetailContent(
        message = message,
        showTopBar = true,
        onBackClick = { navigator.navigateBack() },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageDetailContent(
    message: Message?,
    showTopBar: Boolean = true,
    onBackClick: (() -> Unit)? = null,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (showTopBar) TopAppBar(
                title = {},
                navigationIcon = {
                    if (onBackClick != null) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_back),
                                contentDescription = "Vissza"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                )
            )
        }
    ) { paddingValues ->
        message?.messageDetail?.let { messageDetail ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (isLandscape()) {
                            Modifier.padding(
                                start = BetterNeptunTheme.dimens.extraSmall,
                                end = paddingValues.calculateEndPadding(
                                    LocalLayoutDirection.current
                                ),
                                top = paddingValues.calculateTopPadding(),
                                bottom = paddingValues.calculateBottomPadding()
                            )
                        } else {
                            Modifier.padding(paddingValues)
                        }
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .clip(MaterialTheme.shapes.extraLarge)
                        .verticalScroll(rememberScrollState())
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
                                fontFamily = Armata,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            DetailItem(
                                avatar = message.senderAvatar,
                                value = messageDetail.sender
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            MessageContent(messageDetail)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageContent(
    messageDetail: MessageDetail,
) {
    val localUriHandler = LocalUriHandler.current
    val rawHtml = messageDetail.posts.firstOrNull()?.htmlText ?: ""
    val processedHtml = if (rawHtml.contains("}")) {
        rawHtml.split("}").last()
    } else {
        rawHtml
    }

    HtmlText(
        html = processedHtml,
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
        fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
            ?: Armata,
        onUrlClick = { url ->
            localUriHandler.openUri(url)
        }
    )
}

@Composable
fun DetailItem(avatar: Avatar, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        AvatarImage(
            avatar = avatar,
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = Armata,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@PreviewLightDark
@PreviewWrapper(PreviewThemeProvider::class)
@Composable
fun MessageDetailContentPreview() {
    MessageDetailContent(
        message = Message(
            id = "1",
            name = "Kovács János",
            subject = "Vizsga eredmény",
            date = LocalDateTime.now(),
            isNew = false,
            senderAvatar = Avatar.SystemAvatar,
            messageDetail = MessageDetail(
                subject = "Vizsga eredmény",
                sender = "Kovács János",
                date = LocalDateTime(2023, 10, 25, 14, 30),
                hasUnreadPost = false,
                posts = listOf(
                    MessageDetail.Post(
                        id = "1",
                        htmlText = "Tisztelt Hallgató! <br><br> A vizsgája <b>sikerült</b>. <br><br> Üdvözlettel, <br> Tanár Úr"
                    )
                )
            )
        ),
    )
}
