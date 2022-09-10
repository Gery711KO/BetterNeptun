package hu.kocsisgeri.betterneptun.ui.model

import androidx.annotation.DrawableRes
import hu.kocsisgeri.betterneptun.R
import hu.kocsisgeri.betterneptun.ui.adapter.ListItem

enum class SubjectState(@DrawableRes val res: Int?) {
    BANNED(R.drawable.ic_banned),
    PASS(R.drawable.ic_passed),
    FAILED(R.drawable.ic_failed),
    SIGNED(R.drawable.ic_signed),
    DEFAULT(null)
}

data class MarkBookDataModel(
    val subjectId: Int,
    val subjectCode: String,
    val subjectCredit: String,
    val subjectName: String,
    val subjectRequirement: String,
    val subjectType: String,
    val termId: Int,
    val completed: Boolean,
    val signer: String,
    val values: String,
    val state: SubjectState
): ListItem