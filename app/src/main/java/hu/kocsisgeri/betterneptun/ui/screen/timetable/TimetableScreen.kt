package hu.kocsisgeri.betterneptun.ui.screen.timetable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.alamkanak.weekview.WeekView
import hu.kocsisgeri.betterneptun.R
import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
import hu.kocsisgeri.betterneptun.ui.navigation.destination.CourseDetailDestination
import hu.kocsisgeri.betterneptun.ui.screen.timetable.model.FragmentWeekViewAdapter
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinActivityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableScreen(
    viewModel: TimetableViewModel = koinActivityViewModel(),
    navigator: Navigator = koinInject()
) {
    val events by viewModel.timetableEvents.observeAsState(emptyList())
    val times by viewModel.times.observeAsState("8:22")
    val viewMode by viewModel.viewMode.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.clicked.collect { event ->
            navigator.navigateTo(CourseDetailDestination(event.id.toString()))
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Órarend",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigator::navigateBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Vissza"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val newMode = when (viewMode) {
                            ViewMode.WEEK -> ViewMode.DAY
                            ViewMode.DAY -> ViewMode.WEEK
                        }
                        viewModel.viewMode.tryEmit(newMode)
                    }) {
                        Icon(
                            painter = painterResource(id = viewMode.icon),
                            contentDescription = "Nézet váltás",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val adapter = remember { FragmentWeekViewAdapter(viewModel.clickHandler) }

            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    WeekView(ctx).apply {
                        this.adapter = adapter
                        columnGap = 8.dp.value.toInt()
                        hourHeight = 60.dp.value.toInt()
                        showNowLine = true
                        showNowLineDot = true
                        nowLineDotRadius = 4.dp.value.toInt()
                        
                        // Set initial state
                        numberOfVisibleDays = viewMode.days
                        val splitTimes = times?.split(":")
                        if (splitTimes?.size == 2) {
                            minHour = splitTimes[0].toIntOrNull() ?: 7
                            maxHour = splitTimes[1].toIntOrNull() ?: 23
                        }
                    }
                },
                update = { view ->
                    view.numberOfVisibleDays = viewMode.days
                    val splitTimes = times?.split(":")
                    if (splitTimes?.size == 2) {
                        view.minHour = splitTimes[0].toIntOrNull() ?: 7
                        view.maxHour = splitTimes[1].toIntOrNull() ?: 23
                    }
                    adapter.submitList(events)
                    adapter.refresh()
                }
            )
        }
    }
}
