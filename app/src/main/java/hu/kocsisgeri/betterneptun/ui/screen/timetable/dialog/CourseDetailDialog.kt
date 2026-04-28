package hu.kocsisgeri.betterneptun.ui.screen.timetable.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.madrapps.pikolo.HSLColorPicker
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener
import hu.kocsisgeri.betterneptun.R
import hu.kocsisgeri.betterneptun.domain.model.CalendarEntity
import hu.kocsisgeri.betterneptun.ui.screen.messages.detail.DetailItem
import hu.kocsisgeri.betterneptun.ui.theme.BetterNeptunTheme
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CourseDetailDialog(
    selectedEvent: CalendarEntity.Event?,
    currentColor: Int,
    onDismissRequest: () -> Unit,
    onChangeColor: (CalendarEntity.Event, Int) -> Unit
) {
    var showColorPicker by remember { mutableStateOf(false) }

    selectedEvent?.let { event ->
        Dialog(
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            ),
            onDismissRequest = onDismissRequest
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                shape = MaterialTheme.shapes.extraLarge,
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(Color(currentColor), MaterialTheme.shapes.small)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = event.title.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = onDismissRequest) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_close),
                                contentDescription = "Bezárás"
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            onClick = { showColorPicker = !showColorPicker },
                            shape = MaterialTheme.shapes.medium,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(
                                text = if (showColorPicker) "Mégsem" else "Átállít",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                DetailItem(
                    icon = painterResource(R.drawable.ic_schedule),
                    label = "Időpont",
                    value = getTimeText(event)
                )
                Spacer(modifier = Modifier.height(16.dp))
                DetailItem(
                    icon = painterResource(R.drawable.ic_location),
                    label = "Helyszín",
                    value = event.location.toString()
                )
                Spacer(modifier = Modifier.height(16.dp))
                DetailItem(
                    icon = painterResource(R.drawable.ic_event),
                    label = "Oktató",
                    value = event.teacher
                )
                Spacer(modifier = Modifier.height(16.dp))
                DetailItem(
                    icon = painterResource(R.drawable.ic_course),
                    label = "Tárgykód",
                    value = event.subjectCode
                )
                Spacer(modifier = Modifier.height(16.dp))
                DetailItem(
                    icon = painterResource(R.drawable.ic_course),
                    label = "Kurzuskód",
                    value = event.courseCode
                )

                AnimatedVisibility(visible = showColorPicker) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .height(320.dp),
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            AndroidView(
                                factory = { ctx ->
                                    HSLColorPicker(ctx).apply {
                                        setColor(currentColor)
                                        setColorSelectionListener(object :
                                            SimpleColorSelectionListener() {
                                            override fun onColorSelected(color: Int) {
                                                onChangeColor(event, color)
                                            }
                                        })
                                    }
                                },
                                update = {
                                    it.setColor(currentColor)
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Surface(
                                onClick = {
                                    onChangeColor(event, currentColor)
                                    showColorPicker = false
                                },
                                shape = CircleShape,
                                color = Color(currentColor),
                                modifier = Modifier.size(90.dp),
                                shadowElevation = 8.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "Oké",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
}

@Composable
private fun DetailItem(icon: Painter, label: String, value: String) {
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

private fun getTimeText(event: CalendarEntity.Event): String {
    val day = event.startTime.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("hu"))
    val startMin = event.startTime.minute.let { if (it < 10) "0$it" else it }
    val endMin = event.endTime.minute.let { if (it < 10) "0$it" else it }
    val timeText = "${event.startTime.hour}:${startMin} - ${event.endTime.hour}:${endMin} ($day)"

    return timeText
}

@Preview(showBackground = true, name = "Light Mode")
@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
fun CourseDetailPreview() {
    BetterNeptunTheme {
        CourseDetailDialog(
            selectedEvent = CalendarEntity.Event(
                id = 1,
                title = "Mobil programozás II.",
                courseCode = "VA1_LA_01_MOBIL",
                subjectCode = "NIEVA1FBNE",
                teacher = "Kovács János",
                startTime = LocalDateTime.now().withHour(8).withMinute(0),
                endTime = LocalDateTime.now().withHour(10).withMinute(30),
                location = "BK.1.127",
                color = Color.Blue.toArgb(),
                isAllDay = false,
                isCanceled = false
            ),
            currentColor = android.graphics.Color.BLUE,
            onDismissRequest = {},
            onChangeColor = { _ , _ ->}
        )
    }
}
