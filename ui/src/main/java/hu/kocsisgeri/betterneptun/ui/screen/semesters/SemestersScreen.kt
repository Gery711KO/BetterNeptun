package hu.kocsisgeri.betterneptun.ui.screen.semesters

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.toColorInt
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter
import com.github.mikephil.charting.formatter.LargeValueFormatter
import hu.kocsisgeri.betterneptun.domain.model.ChartColor
import hu.kocsisgeri.betterneptun.domain.model.neptun.ApiResult
import hu.kocsisgeri.betterneptun.ui.core.Navigator
import hu.kocsisgeri.betterneptun.ui.core.theme.BetterNeptunTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun SemestersScreen(
    viewModel: SemestersViewModel = koinViewModel(),
    navigator: Navigator = koinInject(),
) {
    val creditsResult by viewModel.credits.collectAsStateWithLifecycle()
    val averagesResult by viewModel.averages.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }

    SemestersContent(
        selectedTab = selectedTab,
        creditsResult = creditsResult,
        averagesResult = averagesResult,
        onSelectTab = { selectedTab = it },
        onBackClick = { navigator.navigateBack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SemestersContent(
    selectedTab: Int,
    creditsResult: ApiResult<List<Pair<BarDataSet, ChartColor>>>,
    averagesResult: ApiResult<List<Pair<LineDataSet, ChartColor>>>,
    onSelectTab: (Int) -> Unit,
    onBackClick: () -> Unit,
) {
    val tabs = listOf("Kreditek", "Átlagok")

    val colors = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Félévek") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Vissza")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            SecondaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(selectedTab),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { onSelectTab(index) },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTab == index) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedTab) {
                    0 -> when (creditsResult) {
                        is ApiResult.Loading -> LoadingIndicator()
                        is ApiResult.Success -> {
                            val textColor = MaterialTheme.colorScheme.onSurface.toArgb()
                            AndroidView(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                factory = { context ->
                                    BarChart(context).apply {
                                        setupBarChartTheme(textColor)
                                    }
                                },
                                update = { chart ->
                                    val dataSets =
                                        creditsResult.data.map { (set, color) ->
                                            set.apply {
                                                val colorInt = when (color) {
                                                    ChartColor.Primary -> {
                                                        colors.onSurfaceVariant
                                                            .copy(alpha = 0.3f).toArgb()
                                                    }

                                                    ChartColor.Secondary -> colors.onSurface.toArgb()
                                                }
                                                this.color = colorInt
                                            }
                                        }
                                    chart.data = BarData(dataSets).apply {
                                        barWidth = 0.7f
                                        setValueTextColor(textColor)
                                        setValueTextSize(12f)
                                        setValueFormatter(LargeValueFormatter())
                                    }
                                    chart.xAxis.axisMaximum = (dataSets[0].entryCount) + 1f
                                    chart.xAxis.labelCount = (dataSets[0].entryCount) + 1
                                    chart.animateY(1000)
                                    chart.invalidate()
                                }
                            )
                        }

                        is ApiResult.Error -> ErrorMessage(creditsResult.error)
                    }

                    1 -> when (averagesResult) {
                        is ApiResult.Loading -> LoadingIndicator()
                        is ApiResult.Success -> {
                            val textColor = MaterialTheme.colorScheme.onSurface.toArgb()
                            AndroidView(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                factory = { context ->
                                    LineChart(context).apply {
                                        setupLineChartTheme(textColor)
                                    }
                                },
                                update = { chart ->
                                    val dataSets =
                                        averagesResult.data.map { (set, color) ->
                                            set.apply {
                                                val colorInt = when (color) {
                                                    ChartColor.Primary -> "#007541".toColorInt()
                                                    ChartColor.Secondary -> "#096FB3".toColorInt()
                                                }
                                                this.color = colorInt
                                                setCircleColor(colorInt)
                                            }
                                        }
                                    chart.data = LineData(dataSets).apply {
                                        setValueTextColor(textColor)
                                        setValueTextSize(12f)
                                        setValueFormatter(DefaultAxisValueFormatter(2))
                                    }
                                    chart.animateY(1000)
                                    chart.invalidate()
                                }
                            )
                        }

                        is ApiResult.Error -> ErrorMessage(averagesResult.error)
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            modifier = Modifier.size(50.dp),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ErrorMessage(message: String?) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = message ?: "Hiba történt",
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(16.dp)
        )
    }
}

private fun BarChart.setupBarChartTheme(textColor: Int) {
    isHighlightFullBarEnabled = false
    isHighlightPerDragEnabled = false
    isHighlightPerTapEnabled = false
    isDoubleTapToZoomEnabled = false
    description.isEnabled = false
    setPinchZoom(false)
    setDrawBarShadow(false)
    setDrawGridBackground(false)

    val ll1 = LimitLine(30f, "").apply {
        lineWidth = 1f
        lineColor = textColor
        enableDashedLine(10f, 10f, 0f)
        this.textColor = textColor
        labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
        textSize = 10f
    }

    axisLeft.setDrawLimitLinesBehindData(true)
    xAxis.setDrawLimitLinesBehindData(true)
    axisLeft.addLimitLine(ll1)

    legend.apply {
        verticalAlignment = Legend.LegendVerticalAlignment.TOP
        horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        orientation = Legend.LegendOrientation.VERTICAL
        this.textColor = textColor
        setDrawInside(true)
        yOffset = 10f
        xOffset = 10f
        yEntrySpace = 0f
        textSize = 10f
        form = Legend.LegendForm.SQUARE
    }

    xAxis.apply {
        granularity = 1f
        setCenterAxisLabels(false)
        axisMinimum = 0f
        this.textColor = textColor
    }

    axisLeft.apply {
        this.textColor = textColor
        valueFormatter = LargeValueFormatter()
        setDrawGridLines(false)
        spaceTop = 25f
        axisMinimum = 0f
    }

    axisRight.isEnabled = false
}

private fun LineChart.setupLineChartTheme(textColor: Int) {
    isHighlightPerDragEnabled = false
    isHighlightPerTapEnabled = false
    isDoubleTapToZoomEnabled = false
    description.isEnabled = false
    setPinchZoom(false)
    setDrawGridBackground(false)

    val ll1 = LimitLine(5f, "5").apply {
        lineWidth = 1f
        enableDashedLine(10f, 10f, 0f)
        labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP
        this.textColor = textColor
        lineColor = textColor
        textSize = 10f
    }

    axisLeft.setDrawLimitLinesBehindData(true)
    xAxis.setDrawLimitLinesBehindData(true)
    axisLeft.addLimitLine(ll1)

    legend.apply {
        verticalAlignment = Legend.LegendVerticalAlignment.TOP
        horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        orientation = Legend.LegendOrientation.VERTICAL
        this.textColor = textColor
        setDrawInside(true)
        yOffset = 10f
        xOffset = 10f
        yEntrySpace = 0f
        textSize = 10f
        form = Legend.LegendForm.LINE
    }

    xAxis.apply {
        granularity = 1f
        axisMinimum = 0f
        setCenterAxisLabels(false)
        this.textColor = textColor
    }

    axisLeft.apply {
        this.textColor = textColor
        valueFormatter = LargeValueFormatter()
        setDrawGridLines(false)
        spaceTop = 25f
        axisMinimum = 1f
    }

    axisRight.isEnabled = false
}

@Preview(showBackground = true)
@Composable
private fun SemestersScreenSuccessPreview() {
    BetterNeptunTheme {
        val creditsResult = ApiResult.Success(
            listOf(
                BarDataSet(
                    listOf(BarEntry(1f, 30f), BarEntry(2f, 28f)),
                    "Felvett"
                ) to ChartColor.Primary,
                BarDataSet(
                    listOf(BarEntry(1f, 25f), BarEntry(2f, 28f)),
                    "Teljesitett"
                )to ChartColor.Secondary,
            )
        )
        val averagesResult = ApiResult.Success(
            listOf(
                LineDataSet(
                    listOf(Entry(1f, 4.2f), Entry(2f, 3.8f)),
                    "Átlagok"
                ) to ChartColor.Primary,
                LineDataSet(
                    listOf(Entry(1f, 4.2f), Entry(2f, 4.0f)),
                    "Kommultatív átlagok"
                ) to ChartColor.Secondary,
            )
        )

        SemestersContent(
            selectedTab = 0,
            creditsResult = creditsResult,
            averagesResult = averagesResult,
            onSelectTab = {},
            onBackClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SemestersScreenLoadingPreview() {
    BetterNeptunTheme {
        SemestersContent(
            selectedTab = 0,
            creditsResult = ApiResult.Loading,
            averagesResult = ApiResult.Loading,
            onSelectTab = {},
            onBackClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SemestersScreenErrorPreview() {
    BetterNeptunTheme {
        SemestersContent(
            selectedTab = 0,
            creditsResult = ApiResult.Error("Nem sikerült betölteni a krediteket"),
            averagesResult = ApiResult.Error("Nem sikerült betölteni az átlagokat"),
            onSelectTab = {},
            onBackClick = {},
        )
    }
}
