package hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import hu.kocsisgeri.betterneptun.common.utils.now
import hu.kocsisgeri.betterneptun.ui.core.composable.measure.SizeMeasurer
import hu.kocsisgeri.betterneptun.ui.core.composable.measure.SizeMeasurerScope
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.config.WeekViewConfig
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.event.TimeSpan
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.event.WeekData
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.metrics.WeekViewMetrics
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.metrics.rememberWeekViewMetrics
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlin.time.Duration.Companion.hours

@SuppressLint("FrequentlyChangingValue")
@Composable
internal fun WeekBackgroundCompose(
    weekViewConfig: WeekViewConfig,
    weekData: (page: Int) -> WeekData,
    modifier: Modifier = Modifier,
    topContent: @Composable BoxWithConstraintsScope.(PagerState) -> Unit,
    sideContent: @Composable SizeMeasurerScope.(ScrollState, WeekDataHolder) -> Unit,
    gridContent: @Composable ColumnScope.(PagerState, ScrollState, Dp) -> Unit,
) {
    val density = LocalDensity.current

    val maxPages = Int.MAX_VALUE
    val scrollState = rememberScrollState()
    val pagerState = rememberPagerState(initialPage = maxPages / 2) { maxPages }
    val syncedPager = rememberPagerState(initialPage = maxPages / 2) { maxPages }

    val currentItem by remember(pagerState.currentPage) {
        derivedStateOf { weekData(pagerState.currentPage) }
    }

    var disabledInitialScroll by remember { mutableStateOf(false) }

    val metrics = rememberWeekViewMetrics(
        dateRange = currentItem.dateRange,
        timeRange = currentItem.getTimeSpan() ?: TimeSpan.of(LocalTime(6, 0), 12.hours),
        events = currentItem.getSingleEvents(),
        scalingFactor = weekViewConfig.scalingFactor
    )

    val holder = remember(currentItem, metrics) {
        WeekDataHolder(
            week = currentItem,
            metrics = metrics,
            maxWidth = 0.dp
        )
    }

    LaunchedEffect(Unit) {
        if (disabledInitialScroll.not()) {
            scrollState.scrollTo(
                with(density) {
                    (metrics.rowHeightDp * LocalDateTime.now().hour).roundToPx()
                }
            )
        }
        disabledInitialScroll = true
    }

    InfinitePagerHandler(
        pagerState = pagerState,
        syncedPager = syncedPager,
    )

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val measuredThenPlaced = remember(syncedPager) {
            movableContentOf {
                Column {
                    topContent(
                        this@BoxWithConstraints,
                        syncedPager,
                    )
                }
            }
        }
        SizeMeasurer(
            measured = { measuredThenPlaced() }
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                sideContent(
                    scrollState,
                    holder.copy(maxWidth = this@BoxWithConstraints.maxWidth)
                )

                Column(modifier = Modifier.weight(1f)) {
                    measuredThenPlaced()
                    gridContent(
                        pagerState,
                        scrollState,
                        this@BoxWithConstraints.maxWidth
                    )
                }
            }
        }
    }
}

@Composable
private fun InfinitePagerHandler(
    pagerState: PagerState,
    syncedPager: PagerState,
) {

    LaunchedEffect(pagerState) {
        snapshotFlow {
            pagerState.currentPageOffsetFraction
        }.collect {
            syncedPager.scrollToPage(pagerState.currentPage, it)
        }
    }
}

internal data class WeekDataHolder(
    val week: WeekData,
    val metrics: WeekViewMetrics,
    val maxWidth: Dp,
) {

    fun getDynamicWidth(): Dp {
        val availableWidth = maxWidth - metrics.leftOffsetDp
        return if (metrics.columnCount > 0) (availableWidth / metrics.columnCount) else availableWidth
    }
}
