package hu.kocsisgeri.betterneptun.ui.core.composable

import android.util.Base64
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import hu.kocsisgeri.betterneptun.domain.model.Avatar
import hu.kocsisgeri.betterneptun.ui.screen.timetable.model.isColorDark

@Composable
fun AvatarImage(
    avatar: Avatar,
    modifier: Modifier = Modifier,
) {
    when (avatar) {
        is Avatar.Base64Image -> Base64ImageDisplay(avatar.base64ImageString, modifier)
        is Avatar.MonogramAvatar -> MonogramAvatar(avatar, modifier)
        is Avatar.UrlImage -> UrlImage(avatar.imageUrl, modifier)
        Avatar.SystemAvatar -> SystemAvatar(modifier)
    }
}

@Composable
private fun SystemAvatar(modifier: Modifier = Modifier) {
    BoxWithConstraints(
        modifier = modifier.background(MaterialTheme.colorScheme.tertiary),
        contentAlignment = Alignment.Center
    ) {
        val adaptiveSize = max(maxWidth, maxHeight) * 0.6f

        Icon(
            modifier = Modifier.size(adaptiveSize),
            imageVector = Icons.Outlined.Notifications,
            contentDescription = null,
            tint = contentColorFor(MaterialTheme.colorScheme.tertiary)
        )
    }
}

@Composable
private fun MonogramAvatar(
    monogramAvatar: Avatar.MonogramAvatar,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier.background(Color(monogramAvatar.colorLong)),
        contentAlignment = Alignment.Center
    ) {
        val adaptiveFontSize = (maxWidth.value / monogramAvatar.monogram.length)

        Text(
            text = monogramAvatar.monogram,
            fontSize = adaptiveFontSize.sp,
            maxLines = 1,
            color = if (isColorDark(Color(monogramAvatar.colorLong).toArgb())) {
                Color.White
            } else {
                Color.Black
            }
        )
    }
}

@Composable
private fun UrlImage(
    imageUrl: String,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = "Avatar",
        modifier = modifier
    )
}

@Composable
private fun Base64ImageDisplay(
    base64Data: String,
    modifier: Modifier = Modifier,
) {
    val cleanBase64 = base64Data.substringAfter(",")

    val imageBytes by remember(cleanBase64) {
        derivedStateOf {
            decodeBase64ToBitmap(cleanBase64)
        }
    }

    imageBytes?.let {
        AsyncImage(
            model = imageBytes,
            contentDescription = "Avatar",
            modifier = modifier
        )
    }
}

private fun decodeBase64ToBitmap(base64String: String): ByteArray? {
    return try {
        // A Base64 string dekódolása bájtokká
        Base64.decode(base64String, Base64.DEFAULT)
    } catch (e: Exception) {
        null
    }
}