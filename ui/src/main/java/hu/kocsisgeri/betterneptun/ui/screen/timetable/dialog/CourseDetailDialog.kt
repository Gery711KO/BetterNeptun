package hu.kocsisgeri.betterneptun.ui.screen.timetable.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import hu.kocsisgeri.betterneptun.common.utils.now
import hu.kocsisgeri.betterneptun.domain.model.neptun.CalendarItem
import hu.kocsisgeri.betterneptun.ui.R
import hu.kocsisgeri.betterneptun.ui.core.theme.Armata
import hu.kocsisgeri.betterneptun.ui.core.theme.BetterNeptunTheme
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

@Composable
fun CourseDetailDialog(
    selectedEvent: CalendarItem?,
    currentColor: Int,
    onDismissRequest: () -> Unit,
    onEditEvent: (Long?) -> Unit = {},
    onDeleteLocalEvent: (Long) -> Unit = {}
) {
    selectedEvent?.let { event ->
        Dialog(
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            ),
            onDismissRequest = onDismissRequest
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(BetterNeptunTheme.dimens.screenPadding),
                colors = CardDefaults.cardColors(
                    containerColor = BetterNeptunTheme.colorScheme.primaryContainer,
                    contentColor = BetterNeptunTheme.colorScheme.onPrimaryContainer
                ),
                shape = BetterNeptunTheme.shapes.extraLarge,
                elevation = CardDefaults.cardElevation(defaultElevation = BetterNeptunTheme.dimens.default)
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(BetterNeptunTheme.dimens.paddingLarge)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(BetterNeptunTheme.dimens.iconMedium)
                                .clip(BetterNeptunTheme.shapes.small)
                                .background(Color(currentColor))
                        )
                        Spacer(modifier = Modifier.width(BetterNeptunTheme.dimens.itemSpacing))
                        Text(
                            text = event.title,
                            style = BetterNeptunTheme.typography.titleLarge,
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
                    Spacer(modifier = Modifier.height(BetterNeptunTheme.dimens.paddingSmall))

                    DetailItem(
                        icon = painterResource(R.drawable.ic_schedule),
                        label = "Időpont",
                        value = getTimeText(event)
                    )
                    if (event.location.isNullOrBlank().not()) {
                        Spacer(modifier = Modifier.height(BetterNeptunTheme.dimens.paddingMedium))
                        DetailItem(
                            icon = painterResource(R.drawable.ic_location),
                            label = "Helyszín",
                            value = event.location!!
                        )
                    }
                    if (event is CalendarItem.Event) {
                        Spacer(modifier = Modifier.height(BetterNeptunTheme.dimens.paddingMedium))
                        DetailItem(
                            icon = painterResource(R.drawable.ic_event),
                            label = "Oktató",
                            value = event.teacher
                        )
                        Spacer(modifier = Modifier.height(BetterNeptunTheme.dimens.paddingMedium))
                        DetailItem(
                            icon = painterResource(R.drawable.ic_course),
                            label = "Tárgykód",
                            value = event.subjectCode
                        )
                        Spacer(modifier = Modifier.height(BetterNeptunTheme.dimens.paddingMedium))
                        DetailItem(
                            icon = painterResource(R.drawable.ic_course),
                            label = "Kurzuskód",
                            value = event.courseCode
                        )
                    }

                    if (event is CalendarItem.LocalEvent) {
                        Spacer(Modifier.height(BetterNeptunTheme.dimens.paddingMedium))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { onDeleteLocalEvent(event.id) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BetterNeptunTheme.colorScheme.error,
                                    contentColor = BetterNeptunTheme.colorScheme.onError
                                )
                            ) {
                                Text(
                                    text = "Törlés",
                                    style = BetterNeptunTheme.typography.labelMedium
                                )
                            }
                            Spacer(modifier = Modifier.width(BetterNeptunTheme.dimens.paddingSmall))
                            Button(
                                onClick = { onEditEvent(event.id) },
                            ) {
                                Text(
                                    text = "Szerkesztés",
                                    style = BetterNeptunTheme.typography.labelMedium
                                )
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
            shape = BetterNeptunTheme.shapes.medium,
            color = BetterNeptunTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(BetterNeptunTheme.dimens.iconHuge - 8.dp) // 40.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    modifier = Modifier.size(BetterNeptunTheme.dimens.paddingLarge), // 20.dp
                    tint = BetterNeptunTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        Spacer(modifier = Modifier.width(BetterNeptunTheme.dimens.groupSpacing))
        Column {
            Text(
                text = label,
                style = BetterNeptunTheme.typography.labelMedium,
                color = BetterNeptunTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
            )
            Text(
                text = value,
                style = BetterNeptunTheme.typography.titleMedium,
                fontFamily = Armata,
                color = BetterNeptunTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun getTimeText(event: CalendarItem): String {
    val day =  when (event.startTime.dayOfWeek) {
        DayOfWeek.MONDAY -> "hétfő"
        DayOfWeek.TUESDAY -> "kedd"
        DayOfWeek.WEDNESDAY -> "szerda"
        DayOfWeek.THURSDAY -> "csütörtök"
        DayOfWeek.FRIDAY -> "péntek"
        DayOfWeek.SATURDAY -> "szombat"
        DayOfWeek.SUNDAY -> "vasárnap"
    }
    val startMin = event.startTime.minute.let { if (it < 10) "0$it" else it }
    val endMin = event.endTime.minute.let { if (it < 10) "0$it" else it }
    val timeText = "${event.startTime.hour}:${startMin} - ${event.endTime.hour}:${endMin} ($day)"

    return timeText
}

@Preview(showBackground = true, name = "Light Mode")
@Preview(
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
fun CourseDetailPreview() {
    BetterNeptunTheme {
        CourseDetailDialog(
            selectedEvent = CalendarItem.Event(
                id = 1,
                title = "Mobil programozás II.",
                courseCode = "VA1_LA_01_MOBIL",
                subjectCode = "NIEVA1FBNE",
                teacher = "Kovács János",
                startTime = LocalDateTime(LocalDate.now(), LocalTime(8, 0)),
                endTime = LocalDateTime(LocalDate.now(), LocalTime(10, 30)),
                location = "BK.1.127",
                color = Color.Blue.toArgb(),
                isAllDay = false,
                isCanceled = false
            ),
            currentColor = android.graphics.Color.BLUE,
            onDismissRequest = {},
        )
    }
}
