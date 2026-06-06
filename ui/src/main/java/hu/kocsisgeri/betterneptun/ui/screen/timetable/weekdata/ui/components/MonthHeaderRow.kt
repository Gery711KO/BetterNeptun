package hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import hu.kocsisgeri.betterneptun.common.utils.formatMonth
import hu.kocsisgeri.betterneptun.localization.LocalizationKey
import hu.kocsisgeri.betterneptun.localization.localized
import hu.kocsisgeri.betterneptun.ui.core.theme.BetterNeptunTheme
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.MonthNames

@Composable
fun MonthHeaderRow(
    today: LocalDate,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(BetterNeptunTheme.dimens.medium),
    ) {
        Text(
            text = today.formatMonth(
                MonthNames(
                    january = LocalizationKey.MONTH_JANUARY.localized(),
                    february = LocalizationKey.MONTH_FEBRUARY.localized(),
                    march = LocalizationKey.MONTH_MARCH.localized(),
                    april = LocalizationKey.MONTH_APRIL.localized(),
                    may = LocalizationKey.MONTH_MAY.localized(),
                    june = LocalizationKey.MONTH_JUNE.localized(),
                    july =  LocalizationKey.MONTH_JULY.localized(),
                    august = LocalizationKey.MONTH_AUGUST.localized(),
                    september = LocalizationKey.MONTH_SEPTEMBER.localized(),
                    october = LocalizationKey.MONTH_OCTOBER.localized(),
                    november = LocalizationKey.MONTH_NOVEMBER.localized(),
                    december = LocalizationKey.MONTH_DECEMBER.localized(),
                )
            ),
            style = BetterNeptunTheme.typography.headlineSmall
        )
    }
}