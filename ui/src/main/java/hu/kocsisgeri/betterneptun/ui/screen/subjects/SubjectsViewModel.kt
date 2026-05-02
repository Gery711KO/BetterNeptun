package hu.kocsisgeri.betterneptun.ui.screen.subjects

import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.common.launchReportingErrors
import hu.kocsisgeri.betterneptun.domain.model.ApiResult
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.ui.base.ComposeViewModel

class SubjectsViewModel(
    repo: NeptunRepository
) : ComposeViewModel() {

    val listItems = repo.subjects.stateWhileSubscribed()

    init {
        if (repo.subjects.value !is ApiResult.Success) viewModelScope.launchReportingErrors {
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