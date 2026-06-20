package hu.kocsisgeri.betterneptun.ui.screen.subjects

import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.common.utils.launchReportingErrors
import hu.kocsisgeri.betterneptun.domain.error.ErrorRegistry
import hu.kocsisgeri.betterneptun.domain.error.model.ErrorAction
import hu.kocsisgeri.betterneptun.domain.model.UiResult
import hu.kocsisgeri.betterneptun.domain.model.onSuccess
import hu.kocsisgeri.betterneptun.domain.usecase.semester.FetchTermsUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.subjects.FetchSubjectsUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.subjects.GetSubjectsUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.subjects.GetTermsUseCase
import hu.kocsisgeri.betterneptun.localization.LocalizationKey
import hu.kocsisgeri.betterneptun.ui.core.ErrorHandlingComposeViewModel
import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
import hu.kocsisgeri.betterneptun.ui.navigation.destination.HomeDestination
import hu.kocsisgeri.betterneptun.ui.screen.subjects.model.SubjectsScreenUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEach
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class SubjectsViewModel(
    errorRegistry: ErrorRegistry,
    getTermsUseCase: GetTermsUseCase,
    getSubjectsUseCase: GetSubjectsUseCase,
    private val fetchTermsUseCase: FetchTermsUseCase,
    private val fetchSubjectsUseCase: FetchSubjectsUseCase,
    private val navigator: Navigator,
) : ErrorHandlingComposeViewModel(errorRegistry) {

    private val selectedTerm = MutableStateFlow<String?>(null)

    private val termFilterItems = getTermsUseCase(
        onNavigateBackToHome = {
            navigator.navigateBack(to = HomeDestination)
        },
        mapTerm = { term ->
            SubjectsScreenUiModel.FilterItem(
                id = term.id,
                name = term.semesterTitle
            )
        }
    ).registerApiResultToGeneralFullScreenError(
        title = LocalizationKey.ERROR_FETCH_TERMS,
        description = LocalizationKey.ERROR_FETCH_DESCRIPTION,
        primaryAction = ErrorAction.Suspend(
            label = LocalizationKey.ERROR_BUTTON_RETRY,
            action = { fetchTermsUseCase() }
        ),
        secondaryAction = ErrorAction.Normal(
            label = LocalizationKey.ERROR_BUTTON_BACKTOHOME,
            action = ErrorAction.PredefinedAction.NavigateBackToHome
        )
    ).onEach { result ->
        result.onSuccess {
            selectedTerm.value = it.lastOrNull()?.id
        }
    }

    private val subjects = getSubjectsUseCase()
        .registerApiResultToGeneralFullScreenError(
            title = LocalizationKey.ERROR_FETCH_TERMAVERAGES,
            description = LocalizationKey.ERROR_FETCH_DESCRIPTION,
            primaryAction = ErrorAction.Suspend(
                label = LocalizationKey.ERROR_BUTTON_RETRY,
                action = { fetchSubjectsUseCase(selectedTerm.value) }
            ),
            secondaryAction = ErrorAction.Normal(
                label = LocalizationKey.ERROR_BUTTON_BACKTOHOME,
                action = ErrorAction.PredefinedAction.NavigateBackToHome
            )
        )

    val state = combine(
        selectedTerm.filterNotNull(),
        termFilterItems,
        subjects
    ) { selectedTerm, termsFilters, subjects ->
        SubjectsScreenUiModel(
            selectedTermId = selectedTerm,
            filterBar = termsFilters,
            listItems = subjects
        )
    }.stateWhileSubscribed(
        SubjectsScreenUiModel(
            selectedTermId = "",
            filterBar = UiResult.Loading,
            listItems = UiResult.Loading
        )
    )

    init {
        fetchTerms()
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

    private fun handleTermSelection() {
        viewModelScope.launchReportingErrors {
            selectedTerm.filterNotNull().collect { termId ->
                fetchSubjectsUseCase(termId)
            }
        }
    }
}
