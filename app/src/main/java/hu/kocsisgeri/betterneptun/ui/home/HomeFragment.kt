package hu.kocsisgeri.betterneptun.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import hu.kocsisgeri.betterneptun.data.dao.ApiResult
import hu.kocsisgeri.betterneptun.data.repository.course.CourseRepo
import hu.kocsisgeri.betterneptun.databinding.FragmentHomeBinding
import hu.kocsisgeri.betterneptun.ui.adapter.DiffListAdapter
import hu.kocsisgeri.betterneptun.ui.adapter.cell.cellCurrentCourseDelegate
import hu.kocsisgeri.betterneptun.utils.getCourseDateString
import hu.kocsisgeri.betterneptun.utils.setButtonNavigation
import hu.kocsisgeri.betterneptun.utils.showToastOnClick
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModel()
    private val args by navArgs<HomeFragmentArgs>()
    private lateinit var binding: FragmentHomeBinding
    private val listAdapter = DiffListAdapter(cellCurrentCourseDelegate())

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
        setCurrentCourses()
    }

    @SuppressLint("SetTextI18n")
    private fun setUserData() {
        binding.studentNeptun.text = args.currentUser?.neptun
        binding.studentName.text = args.currentUser?.name
        CourseRepo.unreadMessages.asLiveData().observe(viewLifecycleOwner) {
            binding.studentUnread.text = "$it olvasatlan üzenet"
        }
    }

    private fun setCurrentCourses() {
        val snapHelper = PagerSnapHelper()
        binding.currentCourseInfoCard.apply {
            layoutManager =
                LinearLayoutManager(context).apply { orientation = LinearLayoutManager.HORIZONTAL }
            adapter = listAdapter
            snapHelper.attachToRecyclerView(this)
        }

        viewModel.currentCourses.observe(viewLifecycleOwner) {
            listAdapter.updateData(it)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun fetchNewDataOnAppStart() {
        if (!viewModel.isLoggedIn.value) {
            viewModel.fetchData()
        }

        binding.courseLoading.isVisible = true
        binding.nextCourseInfoCard.isVisible = true
        binding.nextCourseRoot.alpha = 0f

        CourseRepo.nextCourse.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResult.Error -> {
                    binding.nextCourseInfoCard.isVisible = false
                    binding.courseLoading.isVisible = false
                }
                is ApiResult.Progress -> {
                    binding.courseLoading.isVisible = true
                }
                is ApiResult.Success -> {
                    binding.nextCourseRoot.alpha = 1f
                    binding.nextCourseInfoCard.isVisible = true
                    binding.courseLoading.isVisible = false
                    binding.nextCourseLabel.text = "Következő óra"
                    binding.nextCourseTitle.text = it.data.title.trimStart()
                    binding.nextCourseLocation.text = it.data.location.trim()
                    binding.nextCourseDate.text = it.data.startTime.getCourseDateString()
                    binding.nextCourseStart.text =
                        "${it.data.startTime.hour}:${
                            if (it.data.startTime.minute < 10) {
                                "0" + it.data.startTime.minute
                            } else it.data.startTime.minute
                        }"
                    binding.nextCourseEnd.text =
                        "${it.data.endTime.hour}:${
                            if (it.data.endTime.minute < 10) {
                                "0" + it.data.endTime.minute
                            } else it.data.endTime.minute
                        }"
                    binding.line.background.setTint(it.data.color)
                }
            }
        }

        /*CourseRepo.currentCourse.distinctUntilChanged().observe(viewLifecycleOwner) {
            binding.lineProgress.max = CourseRepo.currentCourse.value?.getTime()?.ceil() ?: 100
        }

        CourseRepo.currentCourse.observe(viewLifecycleOwner) {
            binding.currentCourseInfoCard.isVisible = it != null
            it?.let {
                binding.lineProgress.progress = it.getPercent()
                binding.currentCourseLabel.text = "Éppen tart"
                binding.lineProgress.setIndicatorColor(it.color)
                binding.currentCourseLocation.text = it.location.trim()
                binding.currentCourseSubject.text = it.title.trimStart()
                binding.currentCourseTimeLeft.text = it.endTime.getTimeLeft()
            }
        }*/
    }

    /*private fun CalendarEntity.Event.getRemainingTime() : Float {
        val diff = endTime.toEpochSecond(ZoneOffset.UTC) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(diff * 1000)
        return seconds / 60f
    }

    private fun CalendarEntity.Event.getTime() : Float {
        val diff = endTime.toEpochSecond(ZoneOffset.UTC) - startTime.toEpochSecond(ZoneOffset.UTC)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(diff * 1000)
        return seconds / 60f
    }

    private fun CalendarEntity.Event.getPercent(): Int {
        return (getTime() - ((getRemainingTime() / getTime()) * 100)).roundToInt()
    }

    private fun Float.ceil() : Int {
        return ceil(this).roundToInt()
    }*/

    private fun setButtons() {
        setButtonNavigation(binding.settingsButtonCard, HomeFragmentDirections.toSettings())
        setButtonNavigation(binding.messageButtonCard, HomeFragmentDirections.toMessages())
        setButtonNavigation(binding.calendarButtonCard, HomeFragmentDirections.toCalendar())
        setButtonNavigation(binding.coursesButtonCard, HomeFragmentDirections.toSubjects())
        setButtonNavigation(binding.semestersButtonCard, HomeFragmentDirections.toSemesters())

        showToastOnClick(binding.examsButtonCard, "Fejlesztés alatt!!")
        showToastOnClick(binding.scheduleButtonCard, "Fejlesztés alatt!!")
    }

    override fun onResume() {
        super.onResume()
        CourseRepo.fetchNew.tryEmit(Unit)
    }
}