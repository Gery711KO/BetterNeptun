package hu.kocsisgeri.betterneptun.ui.screen.subjects

import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.common.utils.launchReportingErrors
import hu.kocsisgeri.betterneptun.domain.model.neptun.ApiResult
import hu.kocsisgeri.betterneptun.domain.usecase.semester.FetchTermsUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.subjects.FetchSubjectsUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.subjects.GetSubjectsUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.subjects.GetTermsUseCase
import hu.kocsisgeri.betterneptun.ui.core.ComposeViewModel
import hu.kocsisgeri.betterneptun.ui.screen.subjects.model.SubjectsScreenUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

class SubjectsViewModel(
    getTermsUseCase: GetTermsUseCase,
    getSubjectsUseCase: GetSubjectsUseCase,
    private val fetchTermsUseCase: FetchTermsUseCase,
    private val fetchSubjectsUseCase: FetchSubjectsUseCase,
) : ComposeViewModel() {

    private val selectedTerm = MutableStateFlow<String?>(null)

    private val termFilterItems = getTermsUseCase { term ->
        SubjectsScreenUiModel.FilterItem(
            id = term.id,
            name = term.semesterTitle
        )
    }

    private val subjects = getSubjectsUseCase()

    val state = combine(
        selectedTerm.filterNotNull(),
        termFilterItems,
        subjects,
    ) { selectedTerm, termsFilters, subjects ->
        SubjectsScreenUiModel(
            selectedTermId = selectedTerm,
            filterBar = termsFilters,
            listItems = subjects
        )
    }.stateWhileSubscribed(
        SubjectsScreenUiModel(
            selectedTermId = "",
            filterBar = ApiResult.Loading,
            listItems = ApiResult.Loading
        )
    )

    init {
        fetchTerms()
        selectDefaultTerm()
        handleTermSelection()
    }

    fun selectTerm(termId: String) {
        selectedTerm.value = termId
    }

    private fun fetchTerms() {
        viewModelScope.launchReportingErrors {
            fetchTermsUseCase()
        }
    }

    private fun selectDefaultTerm() {
        viewModelScope.launchReportingErrors {
            termFilterItems
                .filterIsInstance<ApiResult.Success<List<SubjectsScreenUiModel.FilterItem>>>()
                .first().let {
                    it.data.asReversed().firstOrNull()?.id?.let { termId ->
                        selectedTerm.value = termId
                    }
                }
        }
    }

    private fun handleTermSelection() {
        viewModelScope.launchReportingErrors {
            selectedTerm.collect { termId ->
                fetchSubjectsUseCase(termId)
            }
        }
    }
}
