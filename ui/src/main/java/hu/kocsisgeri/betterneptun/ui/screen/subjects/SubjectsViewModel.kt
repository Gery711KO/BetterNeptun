package hu.kocsisgeri.betterneptun.ui.screen.subjects

import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.common.utils.launchReportingErrors
import hu.kocsisgeri.betterneptun.domain.model.neptun.ApiResult
import hu.kocsisgeri.betterneptun.domain.model.neptun.Term
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.ui.core.ComposeViewModel
import hu.kocsisgeri.betterneptun.ui.screen.subjects.model.SubjectsScreenUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

class SubjectsViewModel(
    private val neptunRepository: NeptunRepository
) : ComposeViewModel() {

    private val selectedTerm = MutableStateFlow<String?>(null)
    private val terms = neptunRepository.terms
    private val subjects = neptunRepository.subjects

    val state = combine(
        selectedTerm.filterNotNull(),
        terms,
        subjects,
    ) { selectedTerm, terms, subjects ->
        SubjectsScreenUiModel(
            selectedTermId = selectedTerm,
            filterBar = when (terms) {
                is ApiResult.Error -> ApiResult.Error(terms.error)
                ApiResult.Loading -> ApiResult.Loading
                is ApiResult.Success<List<Term>> -> ApiResult.Success(
                    data = terms.data.map { term ->
                        SubjectsScreenUiModel.FilterItem(
                            id = term.id,
                            name = term.semesterTitle
                        )
                    }
                )
            },
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
        if (neptunRepository.subjects.value !is ApiResult.Success) {
            viewModelScope.launchReportingErrors {
                neptunRepository.fetchTerms()

                neptunRepository.terms.first().let {
                    when (it) {
                        is ApiResult.Error -> {
                            // TODO handle error
                        }
                        ApiResult.Loading -> {
                            // TODO handle loading
                        }
                        is ApiResult.Success -> it.data.asReversed().firstOrNull()?.id?.let { termId ->
                            selectedTerm.value = termId
                        }
                    }
                }
            }
        }

        viewModelScope.launchReportingErrors {
            selectedTerm.collect { termId ->
                termId?.let {
                    neptunRepository.fetchSubjects(termId)
                }
            }
        }
    }

    fun selectTerm(termId: String) {
        selectedTerm.value = termId
    }
}