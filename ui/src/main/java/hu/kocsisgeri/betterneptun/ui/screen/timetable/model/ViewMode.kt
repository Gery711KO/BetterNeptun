package hu.kocsisgeri.betterneptun.ui.screen.timetable.model

import androidx.annotation.DrawableRes
import hu.kocsisgeri.betterneptun.ui.R

enum class ViewMode(@DrawableRes val icon : Int) {
    FULL_WEEK(R.drawable.ic_fullweek_view),
    WEEK(R.drawable.ic_week_view),
    DAY(R.drawable.ic_day_view),
}
