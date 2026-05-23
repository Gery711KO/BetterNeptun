package hu.kocsisgeri.betterneptun.ui.screen.subjects.model

import androidx.compose.runtime.Immutable
import hu.kocsisgeri.betterneptun.domain.model.neptun.ApiResult
import hu.kocsisgeri.betterneptun.domain.model.neptun.Subject

@Immutable
data class SubjectsScreenUiModel(
    val selectedTermId: String,
    val filterBar: ApiResult<List<FilterItem>>,
    val listItems: ApiResult<List<Subject>>
) {

    data class FilterItem(
        val id: String,
        val name: String,
    )
}