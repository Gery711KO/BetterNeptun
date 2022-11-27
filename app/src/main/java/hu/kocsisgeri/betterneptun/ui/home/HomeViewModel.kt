package hu.kocsisgeri.betterneptun.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.data.dao.ApiResult
import hu.kocsisgeri.betterneptun.data.repository.course.HomeState
import hu.kocsisgeri.betterneptun.data.repository.course.Timer
import hu.kocsisgeri.betterneptun.data.repository.neptun.NeptunRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val neptunRepository: NeptunRepository
) : ViewModel() {

    val isLoggedIn = MutableStateFlow(false)
    val currentCourses = HomeState.currentCourses
    val studentData = neptunRepository.studentData.asLiveData()
    val unreadMessages = HomeState.unreadMessages.asLiveData()
    val refreshProgress = MutableSharedFlow<ApiResult<Unit>>(1, 50)

    fun fetchData() {
        viewModelScope.launch(Dispatchers.IO) {
            HomeState.fetchCalendarTimes()

            HomeState.nextClass.onEach {
                Timer.nextLooper(it)
            }.launchIn(this)

            HomeState.currentClasses.onEach {
                Timer.currentLooper(it.isNotEmpty())
            }.launchIn(this)

            if (!isLoggedIn.value) {
                neptunRepository.fetchCalendarData()
                neptunRepository.fetchMarkBookData()
                neptunRepository.fetchAverages()
                neptunRepository.fetchMessages()
            }
            isLoggedIn.tryEmit(true)
        }
    }

    fun refreshData() {
        viewModelScope.launch(Dispatchers.IO) {
            neptunRepository.login(neptunRepository.currentUser.value).let { result ->
                when (result) {
                    is ApiResult.Error -> refreshProgress.emit(ApiResult.Error(result.error))
                    is ApiResult.Progress -> refreshProgress.emit(ApiResult.Progress(1))
                    is ApiResult.Success -> {
                        HomeState.fetchCalendarTimes()
                        neptunRepository.fetchCalendarData()
                        neptunRepository.fetchMarkBookData()
                        neptunRepository.fetchAverages()
                        neptunRepository.fetchMessages()
                        refreshProgress.emit(ApiResult.Success(Unit))
                    }
                }
            }
        }
    }
}