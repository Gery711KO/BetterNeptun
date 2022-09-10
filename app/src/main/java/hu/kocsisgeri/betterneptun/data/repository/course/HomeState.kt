package hu.kocsisgeri.betterneptun.data.repository.course

import androidx.lifecycle.MutableLiveData
import hu.kocsisgeri.betterneptun.data.dao.ApiResult
import hu.kocsisgeri.betterneptun.data.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.ui.timetable.model.CalendarEntity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.coroutines.CoroutineContext

object HomeState : CoroutineScope, KoinComponent {
    override val coroutineContext: CoroutineContext = Dispatchers.IO

    val fetchNewCurrent = MutableSharedFlow<Unit>(1,100)
    val fetchNewNext = MutableSharedFlow<Unit>(1,100)
    val courses: MutableStateFlow<List<CalendarEntity.Event>> = MutableStateFlow(listOf())
    val nextCourse = MutableLiveData<ApiResult<CalendarEntity.Event>>(ApiResult.Progress(10))
    val currentCourses = MutableStateFlow<List<CalendarEntity.Event>>(listOf())

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentClasses = fetchNewCurrent.onStart { emit(Unit) }.flatMapLatest {
        courses.map { list ->
            list.sortedBy { it.startTime }.filter {
                it.startTime.isBefore(LocalDateTime.now()) && it.endTime.isAfter(LocalDateTime.now())
            }.let {
                Timer.currentEvents = it
                currentCourses.tryEmit(it)
                it
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val nextClass = fetchNewNext.onStart { emit(Unit) }.flatMapLatest {

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

object Timer : CoroutineScope, KoinComponent {

    override val coroutineContext: CoroutineContext = Dispatchers.IO
    var nextEvent: CalendarEntity.Event? = null
    var currentEvents: List<CalendarEntity.Event>? = null

    private const val duration: Long = 10000
    private const val messageFetchInterval : Long = 360000

    private val repo: NeptunRepository by inject()

    fun nextLooper(enabled: Boolean) {
        launch {
            Timber.d("[DEBUG-next-looper]: looped")
            delay(duration)
            withContext(Dispatchers.Main) {
                if (enabled) {
                    nextEvent?.let {
                        HomeState.fetchNewNext.tryEmit(Unit)
                    }
                }
            }
            cancel()
        }
    }

    fun currentLooper(enabled: Boolean) {
        launch {
            Timber.d("[DEBUG-current-looper]: looped")
            delay(duration)
            withContext(Dispatchers.Main) {
                if (enabled) {
                    HomeState.fetchNewCurrent.tryEmit(Unit)
                }
            }
            cancel()
        }
    }

    fun messageFetchLooper(enabled: Boolean) {
        launch {
            delay(duration)
            withContext(Dispatchers.IO) {
                if (enabled) {
                    repo.fetchMessages()
                    messageFetchLooper(enabled)
                }
            }
        }
    }

}