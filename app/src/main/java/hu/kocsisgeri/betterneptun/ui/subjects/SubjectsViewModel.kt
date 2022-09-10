package hu.kocsisgeri.betterneptun.ui.subjects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import hu.kocsisgeri.betterneptun.data.repository.neptun.NeptunRepository

class SubjectsViewModel(
    repo: NeptunRepository
) : ViewModel() {
    private val markBookDataFlow = repo.markBookData
    val listItems = markBookDataFlow.asLiveData()
}