package hu.kocsisgeri.betterneptun.ui.screen.semesters

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import hu.kocsisgeri.betterneptun.domain.model.ChartColor
import hu.kocsisgeri.betterneptun.domain.model.UiResult
import hu.kocsisgeri.betterneptun.localization.LocalizationKey
import hu.kocsisgeri.betterneptun.ui.designsystem.R
import hu.kocsisgeri.betterneptun.ui.designsystem.composable.LoadingLottie
import hu.kocsisgeri.betterneptun.ui.designsystem.composable.RandomWidthBox
import hu.kocsisgeri.betterneptun.ui.designsystem.composable.randomTextSize
import hu.kocsisgeri.betterneptun.ui.designsystem.composable.rememberShimmerProgress
import hu.kocsisgeri.betterneptun.ui.designsystem.composable.sharedShimmer
import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
import hu.kocsisgeri.betterneptun.ui.navigation.modifier.isLandscape
import hu.kocsisgeri.betterneptun.ui.navigation.modifier.sharedBoundsAnimation
import hu.kocsisgeri.betterneptun.ui.screen.semesters.model.ColumnBarData
import hu.kocsisgeri.betterneptun.ui.screen.semesters.model.ColumnBars
import hu.kocsisgeri.betterneptun.ui.screen.semesters.model.LineData
import hu.kocsisgeri.betterneptun.ui.theme.BetterNeptunTheme
import hu.kocsisgeri.betterneptun.ui.theme.PreviewThemeProvider
import hu.kocsisgeri.betterneptun.ui.theme.themeBasedColor
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
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }

    SemestersContent(
        selectedTab = selectedTab,
        creditsResult = uiState.first,
        averagesResult = uiState.second,
        onSelectTab = { selectedTab = it },
        onBackClick = { navigator.navigateBack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SemestersContent(
    selectedTab: Int,
    creditsResult: UiResult<List<ColumnBars>>,
    averagesResult: UiResult<List<LineData>>,
    onSelectTab: (Int) -> Unit,
    onBackClick: () -> Unit,
) {
    val tabs = listOfNotNull(
        "Kreditek",
        "Átlagok"
    )

    if (isLandscape()) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .sharedBoundsAnimation(LocalizationKey.HOME_MENU_SEMESTERS)
        ) {
            Scaffold(
                topBar = { SemestersScreenTopBar(onBackClick) },
                containerColor = colorScheme.background,
                modifier = Modifier.weight(2/5f),
            ) { paddingValues ->
                TabSelector(
                    selectedTab = selectedTab,
                    tabs = tabs,
                    onSelectTab = onSelectTab,
                    isVertical = true,
                    modifier = Modifier.padding(
                        start = paddingValues.calculateStartPadding(LocalLayoutDirection.current),
                        end = BetterNeptunTheme.dimens.extraSmall,
                        top = paddingValues.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding()
                    )
                )
            }
            Box(
                modifier = Modifier
                    .weight(3/5f)
                    .systemBarsPadding()
                    .padding(
                        start = BetterNeptunTheme.dimens.extraSmall,
                        end = WindowInsets.safeContent.asPaddingValues()
                            .calculateEndPadding(LocalLayoutDirection.current)
                    ),
            ) {
                when (selectedTab) {
                    0 -> CreditsChart(creditsResult)
                    1 -> AveragesChart(averagesResult)
                }
            }
        }
    } else {
        Scaffold(
            topBar = { SemestersScreenTopBar(onBackClick) },
            containerColor = colorScheme.background,
            modifier = Modifier
                .fillMaxSize()
                .sharedBoundsAnimation(LocalizationKey.HOME_MENU_SEMESTERS)
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                TabSelector(
                    selectedTab = selectedTab,
                    tabs = tabs,
                    onSelectTab = onSelectTab,
                    isVertical = false
                )

                when (selectedTab) {
                    0 -> CreditsChart(creditsResult)
                    1 -> AveragesChart(averagesResult)
                }
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
            containerColor = Color.Transparent,
            titleContentColor = colorScheme.onSurface,
            navigationIconContentColor = colorScheme.onSurface
        )
    )
}

@Composable
private fun TabSelector(
    selectedTab: Int,
    tabs: List<String>,
    modifier: Modifier = Modifier,
    onSelectTab: (Int) -> Unit,
    isVertical: Boolean = false
) {
    if (isVertical) {
        Column(
            modifier = modifier
                .fillMaxHeight()
                .padding(horizontal = BetterNeptunTheme.dimens.paddingSmall),
            verticalArrangement = Arrangement.spacedBy(BetterNeptunTheme.dimens.paddingSmall)
        ) {
            Spacer(Modifier.height(BetterNeptunTheme.dimens.paddingMedium))
            tabs.forEachIndexed { index, title ->
                val isSelected = selectedTab == index
                NavigationDrawerItem(
                    selected = isSelected,
                    onClick = { onSelectTab(index) },
                    icon = {
                        Icon(
                            painter = painterResource(
                                if (index == 0) R.drawable.ic_courses else R.drawable.ic_semesters
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(BetterNeptunTheme.dimens.iconMedium)
                        )
                    },
                    label = {
                        Text(
                            text = title,
                            style = typography.bodyLarge.copy(
                                fontWeight = if (isSelected) FontWeight.Bold
                                else FontWeight.Normal
                            )
                        )
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = colorScheme.primaryContainer,
                        unselectedContainerColor = Color.Transparent,
                        selectedIconColor = colorScheme.primary,
                        unselectedIconColor = colorScheme.onSurfaceVariant,
                        selectedTextColor = colorScheme.primary,
                        unselectedTextColor = colorScheme.onSurfaceVariant,
                    ),
                    shape = BetterNeptunTheme.shapes.medium
                )
            }
        }
    } else {
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
}

@Composable
private fun AveragesChart(averagesResult: UiResult<List<LineData>>) {
    when (averagesResult) {
        is UiResult.Loading -> LoadingChart()
        is UiResult.Success -> {
            val primaryColor = themeBasedColor(
                darkColor = Color(0xFF00D138),
                lightColor = Color(0xFF00751F)
            )
            val secondaryColor = themeBasedColor(
                darkColor = Color(0xFF04D9FF),
                lightColor = Color(0xFF008BA3)
            )
            val lines = remember(averagesResult, primaryColor, secondaryColor) {
                mapLines(
                    averagesResult = averagesResult,
                    primaryColor = primaryColor,
                    secondaryColor = secondaryColor
                )
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
    }
}

@Composable
private fun CreditsChart(creditsResult: UiResult<List<ColumnBars>>) {
    val colors = colorScheme

    when (creditsResult) {
        is UiResult.Loading -> LoadingChart()
        is UiResult.Success -> {
            val bars = remember(creditsResult, colors) {
                mapBars(
                    creditsResult = creditsResult,
                    colors = colors
                )
            }
            val windowInfo = LocalWindowInfo.current
            val barThickness = remember(bars.size, windowInfo.containerDpSize.height) {
                val availableSizePerBar = windowInfo.containerDpSize.height / maxOf(1, bars.size)
                availableSizePerBar / 3
            }

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
    }
}

@Composable
private fun LoadingChart() {
    val shimmerProgress by rememberShimmerProgress()
    val verticalData = 10
    val horizontalData = 8
    val labels = 2

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Column {
            repeat(labels) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(BetterNeptunTheme.dimens.small)
                ) {
                    Box(
                        modifier = Modifier
                            .size(BetterNeptunTheme.dimens.small)
                            .sharedShimmer(shimmerProgress),
                    )
                    Text(
                        text = randomTextSize(),
                        style = BetterNeptunTheme.typography.labelSmall,
                        modifier = Modifier.sharedShimmer(shimmerProgress)
                    )
                }
                Spacer(Modifier.height(BetterNeptunTheme.dimens.extraSmall / 2))
            }
            Spacer(Modifier.height(BetterNeptunTheme.dimens.medium))
            Row(modifier = Modifier.weight(1f)) {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    repeat(verticalData) {
                        Box(contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .size(BetterNeptunTheme.dimens.small)
                                    .sharedShimmer(shimmerProgress),
                            )
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .padding(start = BetterNeptunTheme.dimens.small)
                        .fillMaxHeight()
                        .weight(1f)
                        .border(
                            BorderStroke(
                                width = 1.dp,
                                color = BetterNeptunTheme.colorScheme.onSurfaceVariant
                                    .copy(alpha = 0.3f)
                            )
                        ),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    repeat(verticalData) {
                        RandomWidthBox(
                            height = BetterNeptunTheme.dimens.large,
                            modifier = Modifier
                                .clip(BetterNeptunTheme.shapes.large.copy(
                                    topStart = CornerSize(0f),
                                    bottomStart = CornerSize(0f)
                                ))
                                .sharedShimmer(
                                    progress = shimmerProgress,
                                    cornerRadius = CornerRadius(0f)
                                )
                        )
                    }
                }
            }
            Spacer(Modifier.height(BetterNeptunTheme.dimens.small))
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                repeat(horizontalData) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(BetterNeptunTheme.dimens.small)
                                .sharedShimmer(shimmerProgress),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun commonLabelProperties(): LabelProperties {
    val primary = colorScheme.primary
    val typo = typography
    return remember(primary, typo) {
        LabelProperties(
            enabled = true,
            textStyle = typo.labelSmall.copy(
                color = primary
            ),
            rotation = LabelProperties.Rotation(degree = 0f)
        )
    }
}

@Composable
private fun commonLabelHelperProperties(): LabelHelperProperties {
    val primary = colorScheme.primary
    val typo = typography
    return remember(primary, typo) {
        LabelHelperProperties(
            textStyle = typo.labelSmall.copy(
                color = primary
            ),
            labelCountPerLine = 1
        )
    }
}

@Composable
private fun commonHorizontalIndicatorProperties(steps: Double): HorizontalIndicatorProperties {
    val primary = colorScheme.primary
    val typo = typography
    return remember(primary, typo, steps) {
        HorizontalIndicatorProperties(
            textStyle = typo.labelSmall.copy(
                color = primary
            ),
            contentBuilder = { it.roundToInt().toString() },
            count = IndicatorCount.StepBased(steps),
        )
    }
}

@Composable
private fun commonVerticalIndicatorProperties(steps: Double): VerticalIndicatorProperties {
    val primary = colorScheme.primary
    val typo = typography
    return remember(primary, typo, steps) {
        VerticalIndicatorProperties(
            textStyle = typo.labelSmall.copy(
                color = primary
            ),
            contentBuilder = { it.roundToInt().toString() },
            count = IndicatorCount.StepBased(steps),
        )
    }
}


@Composable
private fun commonDividerProperties(): DividerProperties {
    val onSurfaceVariant = colorScheme.onSurfaceVariant
    return remember(onSurfaceVariant) {
        DividerProperties(
            xAxisProperties = LineProperties(
                color = SolidColor(onSurfaceVariant.copy(alpha = 0.3f))
            ),
            yAxisProperties = LineProperties(
                color = SolidColor(onSurfaceVariant.copy(alpha = 0.3f))
            )
        )
    }
}

@Composable
private fun commonGridProperties(): GridProperties {
    val onSurfaceVariant = colorScheme.onSurfaceVariant
    return remember(onSurfaceVariant) {
        GridProperties(
            xAxisProperties = GridProperties.AxisProperties(
                color = SolidColor(onSurfaceVariant.copy(alpha = 0.3f))
            ),
            yAxisProperties = GridProperties.AxisProperties(
                lineCount = 2,
                color = SolidColor(onSurfaceVariant.copy(alpha = 0.3f))
            ),
        )
    }
}

@Composable
private fun LoadingIndicator() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        LoadingLottie()
    }
}

private fun mapLines(
    averagesResult: UiResult.Success<List<LineData>>,
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
    creditsResult: UiResult.Success<List<ColumnBars>>,
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
@PreviewWrapper(PreviewThemeProvider::class)
@Composable
private fun SemestersScreenSuccessPreview() {
    BetterNeptunTheme {
        val creditsResult = UiResult.Success(
            listOf(
                ColumnBars(
                    barLabel = "Felvett",
                    bars = listOf(
                        ColumnBarData(
                            label = "2021/2022/1",
                            value = 1.0,
                            color = ChartColor.Primary
                        ),
                        ColumnBarData(
                            label = "2021/2022/1",
                            value = 2.0,
                            color = ChartColor.Primary
                        ),
                    ),
                ),
                ColumnBars(
                    barLabel = "Teljesitett",
                    bars = listOf(
                        ColumnBarData(
                            label = "2021/2022/2",
                            value = 2.0,
                            color = ChartColor.Secondary
                        ),
                        ColumnBarData(
                            label = "2021/2022/2",
                            value = 3.0,
                            color = ChartColor.Secondary
                        ),
                    ),
                ),
            )
        )
        val averagesResult = UiResult.Success(
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
@PreviewWrapper(PreviewThemeProvider::class)
@PreviewScreenSizes
@Composable
private fun SemestersScreenLoadingPreview() {
    SemestersContent(
        selectedTab = 0,
        creditsResult = UiResult.Loading,
        averagesResult = UiResult.Loading,
        onSelectTab = {},
        onBackClick = {},
    )
}

@Preview(
    name = "Phone - Landscape",
    device = "spec:width=411dp,height=891dp,orientation=landscape,dpi=420",
    showSystemUi = true,
)
@PreviewWrapper(PreviewThemeProvider::class)
@Composable
private fun SemestersScreenLandscapePreview() {
    SemestersScreenSuccessPreview()
}
