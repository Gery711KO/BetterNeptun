package hu.kocsisgeri.betterneptun.ui.screen.semesters

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import hu.kocsisgeri.betterneptun.domain.model.ChartColor
import hu.kocsisgeri.betterneptun.domain.model.neptun.ApiResult
import hu.kocsisgeri.betterneptun.localization.LocalizationKey
import hu.kocsisgeri.betterneptun.ui.core.modifier.sharedBoundsAnimation
import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
import hu.kocsisgeri.betterneptun.ui.core.theme.BetterNeptunTheme
import hu.kocsisgeri.betterneptun.ui.core.theme.themeBasedColor
import hu.kocsisgeri.betterneptun.ui.screen.semesters.model.ColumnBarData
import hu.kocsisgeri.betterneptun.ui.screen.semesters.model.ColumnBars
import hu.kocsisgeri.betterneptun.ui.screen.semesters.model.LineData
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.RowChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.Bars.Data.Radius.Rectangle
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.IndicatorCount
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.LineProperties
import ir.ehsannarmani.compose_charts.models.VerticalIndicatorProperties
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import kotlin.math.roundToInt

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
    creditsResult: ApiResult<List<ColumnBars>>,
    averagesResult: ApiResult<List<LineData>>,
    onSelectTab: (Int) -> Unit,
    onBackClick: () -> Unit,
) {
    val tabs = listOf("Kreditek", "Átlagok")

    Scaffold(
        topBar = { SemestersScreenTopBar(onBackClick) },
        containerColor = colorScheme.background,
        modifier = Modifier.sharedBoundsAnimation(LocalizationKey.HOME_MENU_SEMESTERS),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            TabSelector(
                selectedTab = selectedTab,
                tabs = tabs,
                onSelectTab = onSelectTab
            )

            when (selectedTab) {
                0 -> CreditsChart(creditsResult)
                1 -> AveragesChart(averagesResult)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SemestersScreenTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text("Félévek") },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Vissza")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorScheme.background,
            scrolledContainerColor = colorScheme.background,
            titleContentColor = colorScheme.onSurface,
            navigationIconContentColor = colorScheme.onSurface
        )
    )
}

@Composable
private fun TabSelector(
    selectedTab: Int,
    tabs: List<String>,
    onSelectTab: (Int) -> Unit
) {
    SecondaryTabRow(
        selectedTabIndex = selectedTab,
        containerColor = colorScheme.background,
        contentColor = colorScheme.primary,
        indicator = {
            TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(selectedTab),
                color = colorScheme.primary
            )
        }
    ) {
        tabs.forEachIndexed { index, title ->
            val isSelected = selectedTab == index
            Tab(
                selected = isSelected,
                onClick = { onSelectTab(index) },
                text = {
                    Text(
                        text = title,
                        style = typography.bodyLarge.copy(
                            fontWeight = if (isSelected) FontWeight.Bold
                            else FontWeight.Normal
                        ),
                        color = if (isSelected) {
                            colorScheme.primary
                        } else {
                            colorScheme.onSurfaceVariant
                        }
                    )
                }
            )
        }
    }
}

@Composable
private fun AveragesChart(averagesResult: ApiResult<List<LineData>>) {
    when (averagesResult) {
        is ApiResult.Loading -> LoadingIndicator()
        is ApiResult.Success -> {
            val primaryColor = themeBasedColor(
                darkColor = Color(0xFF00D138),
                lightColor = Color(0xFF00751F)
            )
            val secondaryColor = themeBasedColor(
                darkColor = Color(0xFF04D9FF),
                lightColor = Color(0xFF008BA3)
            )
            val lines by remember(averagesResult) {
                derivedStateOf {
                    mapLines(
                        averagesResult = averagesResult,
                        primaryColor = primaryColor,
                        secondaryColor = secondaryColor
                    )
                }
            }

            LineChart(
                data = lines,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                animationMode = AnimationMode.Together { it * 50L },
                animationDelay = 100L,
                minValue = 1.0,
                labelProperties = commonLabelProperties(),
                labelHelperProperties = commonLabelHelperProperties(),
                indicatorProperties = commonHorizontalIndicatorProperties(1.0),
                dividerProperties = commonDividerProperties(),
                gridProperties = commonGridProperties(),
            )
        }

        is ApiResult.Error -> ErrorMessage(averagesResult.error)
    }
}

@Composable
private fun CreditsChart(creditsResult: ApiResult<List<ColumnBars>>) {
    val colors = colorScheme

    when (creditsResult) {
        is ApiResult.Loading -> LoadingIndicator()
        is ApiResult.Success -> {
            val bars by remember(creditsResult) {
                derivedStateOf {
                    mapBars(
                        creditsResult = creditsResult,
                        colors = colors
                    )
                }
            }
            val availableSizePerBar = LocalWindowInfo.current.containerDpSize.height / bars.size
            val barThickness = availableSizePerBar / 3

            RowChart(
                data = bars,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                animationMode = AnimationMode.Together { it * 50L },
                barProperties = BarProperties(
                    cornerRadius = Rectangle(topRight = 6.dp, bottomRight = 6.dp),
                    spacing = -(barThickness / 2f),
                    thickness = barThickness
                ),
                labelProperties = commonLabelProperties(),
                labelHelperProperties = commonLabelHelperProperties(),
                indicatorProperties = commonVerticalIndicatorProperties(5.0),
                dividerProperties = commonDividerProperties(),
                gridProperties = commonGridProperties(),
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                ),
            )
        }

        is ApiResult.Error -> ErrorMessage(creditsResult.error)
    }
}

@Composable
private fun commonLabelProperties() = LabelProperties(
    enabled = true,
    textStyle = typography.labelSmall.copy(
        color = colorScheme.primary
    ),
    rotation = LabelProperties.Rotation(degree = 0f)
)

@Composable
private fun commonLabelHelperProperties() = LabelHelperProperties(
    textStyle = typography.labelSmall.copy(
        color = colorScheme.primary
    ),
    labelCountPerLine = 1
)

@Composable
private fun commonHorizontalIndicatorProperties(steps: Double) = HorizontalIndicatorProperties(
    textStyle = typography.labelSmall.copy(
        color = colorScheme.primary
    ),
    contentBuilder = { it.roundToInt().toString() },
    count = IndicatorCount.StepBased(steps),
)

@Composable
private fun commonVerticalIndicatorProperties(steps: Double) = VerticalIndicatorProperties(
    textStyle = typography.labelSmall.copy(
        color = colorScheme.primary
    ),
    contentBuilder = { it.roundToInt().toString() },
    count = IndicatorCount.StepBased(steps),
)


@Composable
private fun commonDividerProperties() = DividerProperties(
    xAxisProperties = LineProperties(
        color = SolidColor(colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
    ),
    yAxisProperties = LineProperties(
        color = SolidColor(colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
    )
)

@Composable
private fun commonGridProperties() = GridProperties(
    xAxisProperties = GridProperties.AxisProperties(
        color = SolidColor(colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
    ),
    yAxisProperties = GridProperties.AxisProperties(
        lineCount = 2,
        color = SolidColor(colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
    ),
)

    @Composable
private fun LoadingIndicator() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            modifier = Modifier.size(50.dp),
            color = colorScheme.primary
        )
    }
}

@Composable
private fun ErrorMessage(message: String?) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = message ?: "Hiba történt",
            color = colorScheme.error,
            modifier = Modifier.padding(16.dp)
        )
    }
}

private fun mapLines(
    averagesResult: ApiResult.Success<List<LineData>>,
    primaryColor: Color,
    secondaryColor: Color
): List<Line> = averagesResult.data.map { domainLine ->
    Line(
        label = domainLine.label,
        values = domainLine.points,
        color = when (domainLine.color) {
            ChartColor.Primary -> SolidColor(primaryColor)
            ChartColor.Secondary -> SolidColor(secondaryColor)
        },
        firstGradientFillColor = when (domainLine.color) {
            ChartColor.Primary -> primaryColor.copy(alpha = .3f)
            ChartColor.Secondary -> secondaryColor.copy(alpha = .3f)
        },
        secondGradientFillColor = Color.Transparent,
    )
}

private fun mapBars(
    creditsResult: ApiResult.Success<List<ColumnBars>>,
    colors: ColorScheme
): List<Bars> = creditsResult.data.map { domainBars ->
    Bars(
        label = domainBars.barLabel,
        values = domainBars.bars.map { domainBar ->
            Bars.Data(
                label = domainBar.label,
                value = domainBar.value,
                color = when (domainBar.color) {
                    ChartColor.Primary -> SolidColor(
                        colors.onSurfaceVariant.copy(alpha = 0.3f)
                    )

                    ChartColor.Secondary -> SolidColor(colors.onSurface)
                }
            )
        }
    )
}

@PreviewLightDark
@Composable
private fun SemestersScreenSuccessPreview() {
    BetterNeptunTheme {
        val creditsResult = ApiResult.Success(
            listOf(
                ColumnBars(
                    barLabel = "Felvett",
                    bars = listOf(
                        ColumnBarData(label = "2021/2022/1", value = 1.0, color = ChartColor.Primary),
                        ColumnBarData(label = "2021/2022/1", value = 2.0, color = ChartColor.Primary),
                    ),
                ),
                ColumnBars(
                    barLabel = "Teljesitett",
                    bars = listOf(
                        ColumnBarData(label = "2021/2022/2", value = 2.0, color = ChartColor.Secondary),
                        ColumnBarData(label = "2021/2022/2", value = 3.0, color = ChartColor.Secondary),
                    ),
                ),
            )
        )
        val averagesResult = ApiResult.Success(
            listOf(
                LineData(
                    label = "Átlagok",
                    points = listOf(2.0, 3.2),
                    color = ChartColor.Primary
                ),
                LineData(
                    label = "Kommultatív átlagok",
                    points = listOf(3.0, 4.2),
                    color = ChartColor.Primary
                ),
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

@PreviewLightDark
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

@PreviewLightDark
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
