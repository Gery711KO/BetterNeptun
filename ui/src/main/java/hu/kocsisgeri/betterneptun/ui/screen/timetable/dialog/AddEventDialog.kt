package hu.kocsisgeri.betterneptun.ui.screen.timetable.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import hu.kocsisgeri.betterneptun.domain.model.CalendarItem
import hu.kocsisgeri.betterneptun.ui.R
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventDialog(
    event: CalendarItem.LocalEvent?,
    onDismissRequest: () -> Unit,
    onAddEvent: (CalendarItem.LocalEvent) -> Unit
) {
    var title by remember { mutableStateOf(event?.title?: "") }
    var location by remember { mutableStateOf(event?.location?: "") }
    var date by remember { mutableStateOf(event?.startTime?.toLocalDate()?: LocalDate.now()) }
    var startTime by remember { mutableStateOf(event?.startTime?.toLocalTime()?: LocalTime.now()) }
    var endTime by remember { mutableStateOf(event?.endTime?.toLocalTime()?: LocalTime.now().plusMinutes(30)) }
    var selectedColor by remember { mutableIntStateOf(event?.color ?: PREDEFINED_COLORS[5]) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var showColorPopup by remember { mutableStateOf(false) }
    var showCustomColorPicker by remember { mutableStateOf(false) }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy. MM. dd.") }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    if (showCustomColorPicker) {
        val controller = rememberColorPickerController()
        AlertDialog(
            onDismissRequest = { showCustomColorPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedColor = controller.selectedColor.value.toArgb()
                    showCustomColorPicker = false
                }) {
                    Text("OK")
                    Spacer(Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(controller.selectedColor.value)
                            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomColorPicker = false }) { Text("Mégsem") }
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    HsvColorPicker(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        controller = controller,
                        initialColor = Color(selectedColor),
                    )
                    AlphaSlider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(35.dp),
                        controller = controller
                    )
                    BrightnessSlider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(35.dp),
                        controller = controller
                    )
                }
            }
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        date = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Mégsem") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showStartTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = startTime.hour,
            initialMinute = startTime.minute
        )
        AlertDialog(
            onDismissRequest = { showStartTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    startTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                    showStartTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showStartTimePicker = false }) { Text("Mégsem") }
            },
            text = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TimePicker(state = timePickerState)
                }
            }
        )
    }

    if (showEndTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = endTime.hour,
            initialMinute = endTime.minute
        )
        AlertDialog(
            onDismissRequest = { showEndTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    endTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                    showEndTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showEndTimePicker = false }) { Text("Mégsem") }
            },
            text = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TimePicker(state = timePickerState)
                }
            }
        )
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (event == null) "Új esemény hozzáadása" else "Esemény szerkesztése",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Cím") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(painter = painterResource(R.drawable.ic_event), contentDescription = null, modifier = Modifier.size(20.dp))
                    },
                    trailingIcon = {
                        Box(modifier = Modifier.padding(end = 4.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color(selectedColor))
                                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                                    .clickable { showColorPopup = true }
                            )
                            DropdownMenu(
                                expanded = showColorPopup,
                                onDismissRequest = { showColorPopup = false },
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Column(
                                    modifier = Modifier.padding(
                                        top = 12.dp,
                                        start = 12.dp,
                                        end = 12.dp
                                    ),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    PREDEFINED_COLORS.chunked(4).forEach { chunk ->
                                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                            chunk.forEach { color ->
                                                Box(
                                                    modifier = Modifier
                                                        .size(36.dp)
                                                        .clip(CircleShape)
                                                        .background(Color(color))
                                                        .clickable {
                                                            selectedColor = color
                                                            showColorPopup = false
                                                        }
                                                        .then(
                                                            if (selectedColor == color) {
                                                                Modifier.border(
                                                                    width =  2.dp,
                                                                    color = MaterialTheme.colorScheme.primary,
                                                                    shape = CircleShape
                                                                )
                                                            } else Modifier
                                                        )
                                                )
                                            }
                                        }
                                    }

                                    DropdownMenuItem(
                                        text = { Text("Egyéni szín...") },
                                        onClick = {
                                            showColorPopup = false
                                            showCustomColorPicker = true
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        },
                                        trailingIcon = if (selectedColor !in PREDEFINED_COLORS) {
                                            {
                                                Box(
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .clip(CircleShape)
                                                        .background(Color(selectedColor))
                                                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                                                )
                                            }
                                        } else null,
                                        modifier = Modifier.clip(MaterialTheme.shapes.small)
                                    )
                                }
                            }
                        }
                    },
                    singleLine = true
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Helyszín") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_location),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    singleLine = true
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Időpont",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    OutlinedCard(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_calendar),
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Dátum",
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Text(
                                    text = date.format(dateFormatter),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedCard(
                            onClick = { showStartTimePicker = true },
                            modifier = Modifier.weight(1f),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_schedule),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Kezdés",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                    Text(
                                        text = startTime.format(timeFormatter),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }

                        OutlinedCard(
                            onClick = { showEndTimePicker = true },
                            modifier = Modifier.weight(1f),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_schedule),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Vége",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                    Text(
                                        text = endTime.format(timeFormatter),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Mégsem")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val event = CalendarItem.LocalEvent(
                                id = event?.id ?: (System.currentTimeMillis() % 10000000),
                                title = title,
                                startTime = LocalDateTime.of(date, startTime),
                                endTime = LocalDateTime.of(date, endTime),
                                location = location,
                                color = selectedColor,
                            )
                            onAddEvent(event)
                            onDismissRequest()
                        },
                        enabled = title.isNotBlank()
                    ) {
                        Text("Mentés")
                    }
                }
            }
        }
    }
}

private val PREDEFINED_COLORS = listOf(
    0xFFF44336, // Red
    0xFFE91E63, // Pink
    0xFF9C27B0, // Purple
    0xFF673AB7, // Deep Purple
    0xFF3F51B5, // Indigo
    0xFF2196F3, // Blue
    0xFF4CAF50, // Green
    0xFFFF9800  // Orange
).map { it.toInt() }
