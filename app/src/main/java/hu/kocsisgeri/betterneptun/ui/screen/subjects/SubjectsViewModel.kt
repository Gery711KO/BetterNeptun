package hu.kocsisgeri.betterneptun.ui.screen.subjects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.domain.model.ApiResult
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.utils.launchReportingErrors

class SubjectsViewModel(
    repo: NeptunRepository
) : ViewModel() {
    private val markBookDataFlow = repo.subjects
    val listItems = markBookDataFlow.asLiveData()

    init {
        viewModelScope.launchReportingErrors {
            repo.fetchExtendedTerms()

            repo.extendedTerms.collect {
                when (it) {
                    is ApiResult.Error -> {
                        // TODO handle error
                    }
                    ApiResult.Loading -> {
                        // TODO handle loading
                    }
                    is ApiResult.Success -> it.data.firstOrNull()?.termId?.let { termId ->
                        repo.fetchSubjects(termId)
                    }
                }
            }
        }
    }
}