package hu.kocsisgeri.betterneptun.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.data.repository.course.CourseRepo
import hu.kocsisgeri.betterneptun.data.repository.course.Timer
import hu.kocsisgeri.betterneptun.data.repository.neptun.NeptunRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomeViewModel(
    private val neptunRepository: NeptunRepository
) : ViewModel() {

    val isLoggedIn = MutableStateFlow(false)
    val currentCourses = CourseRepo.currentCourses

    fun fetchMessages() {
        viewModelScope.launch(Dispatchers.IO) {
            CourseRepo.fetchCalendarTimes()

            CourseRepo.startTimer.onEach {
                Timer.nextLooper(it == true)
            }.launchIn(this)

            CourseRepo.getCurrent.onEach {
                Timer.currentLooper(it != null)
            }.launchIn(this)

            if (!isLoggedIn.value) neptunRepository.fetchCalendarData()

            neptunRepository.fetchMessages()
            isLoggedIn.tryEmit(true)
        }
    }
}