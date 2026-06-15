package hu.kocsisgeri.betterneptun.ui.screen.timetable.dialog

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
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
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import hu.kocsisgeri.betterneptun.common.utils.formatDatePickerDate
import hu.kocsisgeri.betterneptun.common.utils.formatTimePickerDate
import hu.kocsisgeri.betterneptun.common.utils.now
import hu.kocsisgeri.betterneptun.common.utils.plus
import hu.kocsisgeri.betterneptun.domain.model.neptun.CalendarItem
import hu.kocsisgeri.betterneptun.ui.designsystem.R
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventDialog(
    show: Boolean,
    startDate: LocalDateTime?,
    event: CalendarItem.LocalEvent?,
    onDismissRequest: () -> Unit,
    onAddEvent: (CalendarItem.LocalEvent) -> Unit,
) {
    if (show) {
        val newEventId = remember { System.currentTimeMillis() }

        val title = remember {
            DialogData.TextInputData(
                initialValue = event?.title ?: "",
                label = "Cim",
                leadingIcon = R.drawable.ic_event,
                format = { it }
            )
        }
        val location = remember {
            DialogData.TextInputData(
                initialValue = event?.location ?: "",
                label = "Helyszin",
                leadingIcon = R.drawable.ic_location,
                format = { it }
            )
        }
        val selectedColor = remember {
            DialogData.ColorInput(
                initialValue = event?.color ?: PREDEFINED_COLORS[5],
                withDialog = { input ->
                    ColorPickerAlertDialog(colorInput = input)
                },
                withPopUp = { input ->
                    ColorPickerPopUp(colorInput = input)
                }
            )
        }
        val date = remember {
            DialogData.TextInputData(
                initialValue = event?.startTime ?: startDate ?: LocalDateTime.now(),
                label = "Dátum",
                leadingIcon = R.drawable.ic_calendar,
                format = { it.formatDatePickerDate() },
                withDialog = { input ->
                    DatePickerAlertDialog(
                        initialDate = input.value,
                        onDateSelected = input.onValueChange,
                        onDismissRequest = { input.dialogControl?.onToggleDialog(false) }
                    )
                }
            )
        }
        val startTime = remember {
            DialogData.TextInputData(
                initialValue = event?.startTime ?: startDate ?: LocalDateTime.now(),
                label = "Kezdés",
                leadingIcon = R.drawable.ic_schedule,
                withDialog = { input ->
                    TimePickerAlertDialog(
                        initialTime = input.value,
                        onTimeSelected = input.onValueChange,
                        onDismissRequest = { input.dialogControl?.onToggleDialog(false) }
                    )
                },
                format = { it.time.formatTimePickerDate() }
            )
        }
        val endTime = remember {
            DialogData.TextInputData(
                initialValue = event?.endTime ?: (startDate ?: LocalDateTime.now()).plus(1.hours),
                label = "Vége",
                leadingIcon = R.drawable.ic_schedule,
                withDialog = { input ->
                    TimePickerAlertDialog(
                        initialTime = input.value,
                        onTimeSelected = input.onValueChange,
                        onDismissRequest = { input.dialogControl?.onToggleDialog(false) }
                    )
                },
                format = { it.time.formatTimePickerDate() }
            )
        }

        DialogContent(
            dialogData = DialogData(
                dialogTitle = if (event == null) "Új esemény hozzáadása" else "Esemény szerkesztése",
                dateSectionTitle = "Időpont",
                title = title,
                location = location,
                color = selectedColor,
                date = date,
                startTime = startTime,
                endTime = endTime,
            ),
            onDismissRequest = onDismissRequest,
            onSave = {
                val event = CalendarItem.LocalEvent(
                    id = event?.id ?: newEventId,
                    title = title.formattedValue,
                    startTime = startTime.value,
                    endTime = endTime.value,
                    location = location.formattedValue,
                    color = selectedColor.value,
                )
                onAddEvent(event)
            },
        )
    }
}

@Composable
private fun DialogContent(
    dialogData: DialogData,
    onSave: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    dialogData.PlaceDialogs()

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
            color = MaterialTheme.colorScheme.primaryContainer,
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
                    text = dialogData.dialogTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                CustomTextField(
                    value = dialogData.title.formattedValue,
                    label = dialogData.title.label,
                    onValueChange = dialogData.title.onValueChange,
                    leadingIcon = painterResource(dialogData.title.leadingIcon),
                    trailingIcon = {
                        TitleLeadingIcon(colorInput = dialogData.color)
                    }
                )
                CustomTextField(
                    value = dialogData.location.value,
                    onValueChange = dialogData.location.onValueChange,
                    label = dialogData.location.label,
                    leadingIcon = painterResource(dialogData.location.leadingIcon)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = dialogData.dateSectionTitle,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    TextFieldMimicCard(
                        icon = painterResource(dialogData.date.leadingIcon),
                        label = dialogData.date.label,
                        value = dialogData.date.formattedValue,
                        onClick = { dialogData.date.dialogControl?.onToggleDialog(true) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextFieldMimicCard(
                            icon = painterResource(dialogData.startTime.leadingIcon),
                            label = dialogData.startTime.label,
                            value = dialogData.startTime.formattedValue,
                            onClick = { dialogData.startTime.dialogControl?.onToggleDialog(true) },
                            modifier = Modifier.weight(1f)
                        )

                        TextFieldMimicCard(
                            icon = painterResource(dialogData.endTime.leadingIcon),
                            label = dialogData.endTime.label,
                            value = dialogData.endTime.formattedValue,
                            onClick = { dialogData.endTime.dialogControl?.onToggleDialog(true) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                DialogActions(
                    isSaveEnabled = dialogData.title.value.isNotBlank(),
                    onDismissRequest = onDismissRequest,
                    onSave = onSave
                )
            }
        }
    }
}

@Composable
private fun TitleLeadingIcon(
    colorInput: DialogData.ColorInput,
) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(Color(colorInput.value))
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline,
                CircleShape
            )
            .clickable { colorInput.popUpControl.onToggleDialog(true) }
    )
    colorInput.popUpControl.Content()
}

@Composable
private fun ColorPickerPopUp(colorInput: DialogData.ColorInput) {
    DropdownMenu(
        expanded = colorInput.popUpControl.show,
        onDismissRequest = { colorInput.popUpControl.onToggleDialog(false) },
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
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
                                    colorInput.onValueChange(color)
                                    colorInput.popUpControl.onToggleDialog(false)
                                }
                                .then(
                                    if (colorInput.value == color) {
                                        Modifier.border(
                                            width = 2.dp,
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
                    colorInput.dialogControl.onToggleDialog(true)
                    colorInput.popUpControl.onToggleDialog(false)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = if (colorInput.value !in PREDEFINED_COLORS) {
                    {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(colorInput.value))
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outline,
                                    CircleShape
                                )
                        )
                    }
                } else null,
                modifier = Modifier.clip(MaterialTheme.shapes.small)
            )
        }
    }
}

@Composable
private fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: Painter,
    trailingIcon: @Composable (BoxScope.() -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = {
            Icon(
                painter = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = trailingIcon?.let { content ->
            {
                Box(modifier = Modifier.padding(end = 4.dp)) {
                    content()
                }
            }
        },
        singleLine = true,
    )
}

@Composable
private fun TextFieldMimicCard(
    icon: Painter,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedCard(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outline),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = icon,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun DialogActions(
    isSaveEnabled: Boolean,
    onSave: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DialogTextButton(
            text = "Mégsem",
            onClick = onDismissRequest
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = {
                onSave()
                onDismissRequest()
            },
            enabled = isSaveEnabled
        ) {
            Text(
                text = "Mentés",
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun ColorPickerAlertDialog(colorInput: DialogData.ColorInput) {
    val controller = rememberColorPickerController()

    AlertDialog(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        onDismissRequest = { colorInput.dialogControl.onToggleDialog(false) },
        confirmButton = {
            TextButton(
                onClick = {
                    colorInput.onValueChange(controller.selectedColor.value.toArgb())
                    colorInput.dialogControl.onToggleDialog(false)
                }
            ) {
                Text(
                    text = "OK",
                    style = MaterialTheme.typography.labelMedium
                )
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
            DialogTextButton(
                text = "Mégsem",
                onClick = { colorInput.dialogControl.onToggleDialog(false) }
            )
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
                    initialColor = Color(colorInput.value),
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

@Composable
private fun DatePickerAlertDialog(
    initialDate: LocalDateTime,
    onDateSelected: (LocalDateTime) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate
            .date.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
    )

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            DialogTextButton(
                text = "OK",
                onClick = {
                    datePickerState.selectedDateMillis?.let {
                        onDateSelected(
                            Instant.fromEpochMilliseconds(it)
                                .toLocalDateTime(TimeZone.currentSystemDefault())
                        )
                    }
                    onDismissRequest()
                }
            )
        },
        dismissButton = {
            DialogTextButton(
                text = "Mégsem",
                onClick = onDismissRequest
            )
        },
        colors = DatePickerDefaults.colors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        )
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
            showModeToggle = false
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerAlertDialog(
    initialTime: LocalDateTime,
    onTimeSelected: (LocalDateTime) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute
    )

    AlertDialog(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        onDismissRequest = onDismissRequest,
        confirmButton = {
            DialogTextButton(
                text = "OK",
                onClick = {
                    onTimeSelected(
                        LocalDateTime(
                            initialTime.date,
                            LocalTime(timePickerState.hour, timePickerState.minute)
                        )
                    )
                    onDismissRequest()
                }
            )
        },
        dismissButton = {
            DialogTextButton(
                text = "Mégsem",
                onClick = onDismissRequest
            )
        },
        text = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TimePicker(state = timePickerState)
            }
        }
    )
}


@Composable
fun DialogTextButton(
    text: String,
    onClick: () -> Unit,
) {
    TextButton(onClick = onClick) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

private class DialogData(
    val dialogTitle: String,
    val dateSectionTitle: String,
    val title: TextInputData<String>,
    val location: TextInputData<String>,
    val color: ColorInput,
    val date: TextInputData<LocalDateTime>,
    val startTime: TextInputData<LocalDateTime>,
    val endTime: TextInputData<LocalDateTime>,
) {
    @Immutable
    data class ColorInput(
        val initialValue: Int,
        val withDialog: @Composable (ColorInput) -> Unit,
        val withPopUp: @Composable (ColorInput) -> Unit,
    ) {
        var value by mutableIntStateOf(initialValue)
        val onValueChange: (Int) -> Unit = {
            value = it
        }

        val dialogControl by derivedStateOf {
            DialogControl(this) {
                withDialog(it)
            }
        }

        val popUpControl by derivedStateOf {
            DialogControl(this) {
                withPopUp(it)
            }
        }
    }

    @Immutable
    data class TextInputData<T>(
        private val initialValue: T,
        val label: String,
        val withDialog: (@Composable (TextInputData<T>) -> Unit)? = null,
        @DrawableRes val leadingIcon: Int,
        val format: (T) -> String
    ) {
        var value by mutableStateOf(initialValue)
        val onValueChange: (T) -> Unit = {
            value = it
        }

        val dialogControl by derivedStateOf {
            withDialog?.let { content ->
                DialogControl(this) {
                    content(it)
                }
            }
        }

        val formattedValue by derivedStateOf { format(value) }
    }

    @Immutable
    class DialogControl<T>(
        val value: T,
        private val content: @Composable DialogControl<T>.(T) -> Unit
    ) {
        var show by mutableStateOf(false)
        val onToggleDialog: (Boolean) -> Unit = {
            show = it
        }

        @Composable
        fun Content() {
            if (show) content(value)
        }
    }

    @Composable
    fun PlaceDialogs() {
        title.dialogControl?.Content()
        location.dialogControl?.Content()
        color.dialogControl.Content()
        date.dialogControl?.Content()
        startTime.dialogControl?.Content()
        endTime.dialogControl?.Content()
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
