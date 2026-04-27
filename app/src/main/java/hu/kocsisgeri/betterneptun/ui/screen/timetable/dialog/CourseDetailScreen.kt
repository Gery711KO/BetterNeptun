package hu.kocsisgeri.betterneptun.ui.screen.timetable.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.madrapps.pikolo.HSLColorPicker
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener
import hu.kocsisgeri.betterneptun.R
import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
import hu.kocsisgeri.betterneptun.ui.screen.timetable.TimetableViewModel
import hu.kocsisgeri.betterneptun.ui.screen.timetable.model.CalendarEntity
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinActivityViewModel
import java.time.format.TextStyle
import java.util.*
import androidx.compose.ui.tooling.preview.Preview
import hu.kocsisgeri.betterneptun.ui.theme.BetterNeptunTheme
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    viewModel: TimetableViewModel = koinActivityViewModel(),
    navigator: Navigator = koinInject(),
) {
    val selectedEvent by viewModel.getSelectedEvent().collectAsStateWithLifecycle()
    
    CourseDetailContent(
        selectedEvent = selectedEvent,
        onBack = { navigator.navigateBack() },
        onChangeColor = { event, color ->
            viewModel.changeColor(event, color)
            viewModel.selectEvent(event.copy(color = color))
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailContent(
    selectedEvent: CalendarEntity.Event?,
    onBack: () -> Unit,
    onChangeColor: (CalendarEntity.Event, Int) -> Unit
) {
    var showColorPicker by remember { mutableStateOf(false) }
    var currentColor by remember(selectedEvent) { mutableIntStateOf(selectedEvent?.color ?: 0) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Óra részletei",
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = hu.kocsisgeri.betterneptun.ui.theme.Armata,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Vissza"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
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
            selectedEvent?.let { event ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
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
                        Column(modifier = Modifier.padding(20.dp)) {
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
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontFamily = hu.kocsisgeri.betterneptun.ui.theme.Armata,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
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
                            Spacer(modifier = Modifier.height(20.dp))

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
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    AnimatedVisibility(
                        visible = showColorPicker,
                        enter = scaleIn(animationSpec = tween(200)),
                        exit = scaleOut(animationSpec = tween(200))
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
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
                                            setColorSelectionListener(object : SimpleColorSelectionListener() {
                                                override fun onColorSelected(color: Int) {
                                                    currentColor = color
                                                }
                                            })
                                        }
                                    },
                                    modifier = Modifier.size(280.dp)
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

                    Spacer(modifier = Modifier.height(32.dp))
                }
            } ?: Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
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

@Preview(showBackground = true, name = "Light Mode")
@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
fun CourseDetailPreview() {
    BetterNeptunTheme {
        CourseDetailContent(
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
            onBack = {},
            onChangeColor = { _, _ -> }
        )
    }
}

@Preview(showBackground = true, name = "Loading State")
@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
fun CourseDetailLoadingPreview() {
    BetterNeptunTheme {
        CourseDetailContent(
            selectedEvent = null,
            onBack = {},
            onChangeColor = { _, _ -> }
        )
    }
}
