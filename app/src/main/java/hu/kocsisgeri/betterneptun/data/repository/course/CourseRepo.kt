package hu.kocsisgeri.betterneptun.data.repository.course

import androidx.lifecycle.MutableLiveData
import hu.kocsisgeri.betterneptun.ui.timetable.model.CalendarEntity
import hu.kocsisgeri.betterneptun.utils.*
import hu.kocsisgeri.betterneptun.utils.data_manager.DataManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.coroutines.CoroutineContext

object CourseRepo : CoroutineScope, KoinComponent {
    override val coroutineContext: CoroutineContext = Dispatchers.IO

    private val dataManager: DataManager by inject()

    val fetchNew = MutableSharedFlow<Unit>(1,100)
    val courses: MutableStateFlow<List<CalendarEntity.Event>> = MutableStateFlow(listOf())
    val nextCourse = MutableLiveData<CalendarEntity.Event>()
    val currentCourse = MutableLiveData<CalendarEntity.Event>()

    @OptIn(ExperimentalCoroutinesApi::class)
    val getCurrent = fetchNew.onStart { emit(Unit) }.flatMapLatest {
        courses.map { list ->
            list.sortedBy { it.startTime }.firstOrNull {
                it.startTime.isBefore(LocalDateTime.now()) && it.endTime.isAfter(LocalDateTime.now())
            }.let {
                Timer.currentEvent = it
                currentCourse.postValue(it)
                it
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val startTimer = fetchNew.onStart { emit(Unit) }.flatMapLatest {
        courses.map { list ->
            list.sortedBy { it.startTime }.firstOrNull {
                it.startTime.isAfter(LocalDateTime.now())
            }.let { event ->
                nextCourse.postValue(event)
                Timer.nextEvent = event
                event?.startTime
                    ?.toEpochSecond(ZoneOffset.UTC)
                    ?.minus(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
                    ?.let { it < 3600000 }
            }
        }
    }

    val unreadMessages = MutableStateFlow(0)
    val firstClassTime = MutableStateFlow<String?>(null)
    val lastClassTime = MutableStateFlow<String?>(null)
    val isTimelineAutomatic = MutableStateFlow<Boolean?>(null)

    fun fetchCalendarTimes() {
        dataManager.sharedPreferences.get(PREF_IS_TIMELINE_AUTOMATIC, true).let {
            isTimelineAutomatic.tryEmit(it)
        }
        dataManager.sharedPreferences.get(PREF_FIRST_CLASS_TIME, "8:00").let {
            firstClassTime.tryEmit(it)
        }

        dataManager.sharedPreferences.get(PREF_LAST_CLASS_TIME, "23:00").let {
            lastClassTime.tryEmit(it)
        }
    }

    init {
        firstClassTime.onEach {
            it?.let {
                val save = dataManager.getDefault(PREF_STAY_LOGGED_ID, false)
                if (save) dataManager.sharedPreferences.put(PREF_FIRST_CLASS_TIME, it)
            }
        }.launchIn(this)

        lastClassTime.onEach {
            it?.let {
                val save = dataManager.getDefault(PREF_STAY_LOGGED_ID, false)
                if (save) dataManager.sharedPreferences.put(PREF_LAST_CLASS_TIME, it)
            }
        }.launchIn(this)

        isTimelineAutomatic.onEach {
            it?.let {
                val save = dataManager.getDefault(PREF_STAY_LOGGED_ID, false)
                if (save) dataManager.sharedPreferences.put(PREF_IS_TIMELINE_AUTOMATIC, it)
            }
        }.launchIn(this)
    }
}

object Timer : CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.IO
    var nextEvent: CalendarEntity.Event? = null
    var currentEvent: CalendarEntity.Event? = null
    var duration: Long = 60000

    fun nextLooper(enabled: Boolean) {
        launch {
            delay(duration)
            withContext(Dispatchers.Main) {
                if (enabled) {
                    CourseRepo.nextCourse.postValue(nextEvent)
                    CourseRepo.fetchNew.tryEmit(Unit)
                    nextLooper(enabled)
                    this@launch.cancel()
                    this.cancel()
                }
            }
        }
    }

    fun currentLooper(enabled: Boolean) {
        launch {
            delay(duration)
            withContext(Dispatchers.Main) {
                if (enabled) {
                    CourseRepo.currentCourse.postValue(nextEvent)
                    CourseRepo.fetchNew.tryEmit(Unit)
                    currentLooper(enabled)
                    this@launch.cancel()
                    this.cancel()
                }
            }
        }
    }

}