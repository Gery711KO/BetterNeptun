package hu.kocsisgeri.betterneptun.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.distinctUntilChanged
import androidx.navigation.fragment.navArgs
import hu.kocsisgeri.betterneptun.R
import hu.kocsisgeri.betterneptun.data.repository.course.CourseRepo
import hu.kocsisgeri.betterneptun.databinding.FragmentHomeBinding
import hu.kocsisgeri.betterneptun.ui.timetable.model.CalendarEntity
import hu.kocsisgeri.betterneptun.utils.getCourseDateString
import hu.kocsisgeri.betterneptun.utils.getTimeLeft
import hu.kocsisgeri.betterneptun.utils.setButtonNavigation
import hu.kocsisgeri.betterneptun.utils.showToastOnClick
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit
import kotlin.math.ceil
import kotlin.math.roundToInt

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModel()

    private val args by navArgs<HomeFragmentArgs>()

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUserData()
        setButtons()
        fetchNewDataOnAppStart()
    }

    @SuppressLint("SetTextI18n")
    private fun setUserData() {
        binding.studentNeptun.text = args.currentUser?.neptun
        binding.studentName.text = args.currentUser?.name
        binding.studentUnread.text = "${args.currentUser?.unreadMessages} olvasatlan üzenet"
    }

    @SuppressLint("SetTextI18n")
    private fun fetchNewDataOnAppStart() {
        if (!viewModel.isLoggedIn.value) {
            viewModel.fetchMessages()
        }

        CourseRepo.nextCourse.observe(viewLifecycleOwner) {
            binding.nextCourseInfoCard.isVisible = it != null
            it?.let {
                binding.courseLoading.isVisible = false
                binding.nextCourseLabel.text = "Következő óra"
                binding.nextCourseTitle.text = it.title.trimStart()
                binding.nextCourseLocation.text = it.location.trim()
                binding.nextCourseDate.text = it.startTime.getCourseDateString()
                binding.nextCourseStart.text =
                    "${it.startTime.hour}:${
                        if (it.startTime.minute < 10) {
                            "0" + it.startTime.minute
                        } else it.startTime.minute
                    }"
                binding.nextCourseEnd.text =
                    "${it.endTime.hour}:${
                        if (it.endTime.minute < 10) {
                            "0" + it.endTime.minute
                        } else it.endTime.minute
                    }"
                binding.line.background.setTint(it.color)
            }
        }

        CourseRepo.currentCourse.distinctUntilChanged().observe(viewLifecycleOwner) {
            binding.lineProgress.max = CourseRepo.currentCourse.value?.let {
                it.getTimeDiff(it.startTime).roundToInt()
            }?: 100
        }

        CourseRepo.currentCourse.observe(viewLifecycleOwner) {
            binding.currentCourseInfoCard.isVisible = it != null
            it?.let {
                binding.lineProgress.progress =
                    (it.getTimeDiff(it.startTime) / it.getTimeDiff(LocalDateTime.now())).roundToInt()
                binding.currentCourseLabel.text = "Éppen tart"
                binding.lineProgress.setIndicatorColor(it.color)
                binding.currentCourseLocation.text = it.location.trim()
                binding.currentCourseSubject.text = it.title.trimStart()
                binding.currentCourseTimeLeft.text = it.endTime.getTimeLeft()
            }
        }
    }

    private fun CalendarEntity.Event.getTimeDiff(otherDate: LocalDateTime): Float {
        val diff = endTime.toEpochSecond(ZoneOffset.UTC) - otherDate.toEpochSecond(ZoneOffset.UTC)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(diff * 1000)
        return ceil(seconds / 60f)
    }

    private fun setButtons() {
        setButtonNavigation(binding.settingsButtonCard, HomeFragmentDirections.toSettings())
        setButtonNavigation(binding.messageButtonCard, HomeFragmentDirections.toMessages())
        setButtonNavigation(binding.calendarButtonCard, HomeFragmentDirections.toCalendar())

        showToastOnClick(binding.coursesButtonCard, "Fejlesztés alatt!!")
        showToastOnClick(binding.examsButtonCard, "Fejlesztés alatt!!")
        showToastOnClick(binding.semestersButtonCard, "Fejlesztés alatt!!")
        showToastOnClick(binding.scheduleButtonCard, "Fejlesztés alatt!!")
    }

    override fun onResume() {
        super.onResume()
        CourseRepo.fetchNew.tryEmit(Unit)
    }
}