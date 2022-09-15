package hu.kocsisgeri.betterneptun.data.repository.course

import androidx.lifecycle.MutableLiveData
import hu.kocsisgeri.betterneptun.data.dao.ApiResult
import hu.kocsisgeri.betterneptun.ui.timetable.model.CalendarEntity
import hu.kocsisgeri.betterneptun.utils.*
import hu.kocsisgeri.betterneptun.utils.data_manager.DataManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.coroutines.CoroutineContext

object CourseRepo : CoroutineScope, KoinComponent {
    override val coroutineContext: CoroutineContext = Dispatchers.IO

    private val dataManager: DataManager by inject()

    val fetchNew = MutableSharedFlow<Unit>(1,100)
    val courses: MutableStateFlow<List<CalendarEntity.Event>> = MutableStateFlow(listOf())
    val nextCourse = MutableLiveData<ApiResult<CalendarEntity.Event>>(ApiResult.Progress(10))
    val currentCourses = MutableLiveData<List<CalendarEntity.Event>>()

    @OptIn(ExperimentalCoroutinesApi::class)
    val getCurrent = fetchNew.onStart { emit(Unit) }.flatMapLatest {
        courses.map { list ->
            list.sortedBy { it.startTime }.filter {
                it.startTime.isBefore(LocalDateTime.now()) && it.endTime.isAfter(LocalDateTime.now())
            }.let {
                Timer.currentEvents = it
                currentCourses.postValue(it)
                it
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val startTimer = fetchNew.onStart { emit(Unit) }.flatMapLatest {

        courses.map { list ->
            if (list.isNotEmpty()) {
                list.sortedBy { it.startTime }.firstOrNull {
                    it.startTime.isAfter(LocalDateTime.now())
                }.let { event ->
                    if (event != null) {
                        nextCourse.postValue(ApiResult.Success(event))
                        Timer.nextEvent = event
                        event.startTime
                            .toEpochSecond(ZoneOffset.UTC)
                            .minus(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
                            .let { it < 3600000 }
                    } else {
                        nextCourse.postValue(ApiResult.Error("There is no such event"))
                        false
                    }
                }
            } else {
                ApiResult.Error("Empty")
                false
            }
        }
    }

    val unreadMessages = MutableStateFlow(0)
    val firstClassTime = MutableStateFlow<String?>(null)
    val lastClassTime = MutableStateFlow<String?>(null)
    val isTimelineAutomatic = MutableStateFlow<Boolean?>(true)

    fun fetchCalendarTimes() {
        courses.onEach { list ->
            if (list.isNotEmpty()) {
                list.map { event ->
                    event.startTime.hour
                }.min().let {
                    Timber.d("[DEBUG-firstTime]: $it")
                    firstClassTime.tryEmit("$it:00")
                }

                list.map { event ->
                    event.endTime.hour
                }.max().let {
                    Timber.d("[DEBUG-lastTime]: $it")
                    lastClassTime.tryEmit("${it + 1}:00")
                }
            }
        }.launchIn(this)
    }
}

object Timer : CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.IO
    var nextEvent: CalendarEntity.Event? = null
    var currentEvents: List<CalendarEntity.Event>? = null
    var duration: Long = 60000

    fun nextLooper(enabled: Boolean) {
        launch {
            delay(duration)
            withContext(Dispatchers.Main) {
                if (enabled) {
                    nextEvent?.let {
                        CourseRepo.nextCourse.postValue(ApiResult.Success(it))
                        CourseRepo.fetchNew.tryEmit(Unit)
                        nextLooper(enabled)
                        this@launch.cancel()
                        this.cancel()
                    }
                }
            }
        }
    }

    fun currentLooper(enabled: Boolean) {
        launch {
            delay(duration)
            withContext(Dispatchers.Main) {
                if (enabled) {
                    CourseRepo.currentCourses.postValue(currentEvents)
                    CourseRepo.fetchNew.tryEmit(Unit)
                    currentLooper(enabled)
                    this@launch.cancel()
                    this.cancel()
                }
            }
        }
    }

}