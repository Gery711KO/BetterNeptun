package hu.kocsisgeri.betterneptun.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import hu.kocsisgeri.betterneptun.data.dao.ApiResult
import hu.kocsisgeri.betterneptun.data.repository.course.HomeState
import hu.kocsisgeri.betterneptun.databinding.FragmentHomeBinding
import hu.kocsisgeri.betterneptun.ui.adapter.DiffListAdapter
import hu.kocsisgeri.betterneptun.ui.adapter.cell.cellCurrentCourseDelegate
import hu.kocsisgeri.betterneptun.ui.main.MainActivity
import hu.kocsisgeri.betterneptun.utils.getCourseDateString
import hu.kocsisgeri.betterneptun.utils.setButtonNavigation
import hu.kocsisgeri.betterneptun.utils.showToastOnClick
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModel()
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
        setupPullToRefresh()
    }

    @SuppressLint("SetTextI18n")
    private fun setUserData() {
        viewModel.studentData.observe(viewLifecycleOwner) {
            binding.studentName.text = it?.name
            binding.studentNeptun.text = it?.neptun
        }

        viewModel.unreadMessages.observe(viewLifecycleOwner) {
            if (it != 0) {
                binding.studentUnread.isVisible = true
                binding.studentUnread.text = it.toString()
            } else binding.studentUnread.isVisible = false
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

        viewModel.currentCourses.onEach {
            listAdapter.updateData(it)
        }.launchIn(lifecycleScope)
    }

    private fun setupPullToRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshData()
        }

        viewModel.refreshProgress.onEach {
            when (it) {
                is ApiResult.Error -> {
                    Toast.makeText(context, it.error, Toast.LENGTH_SHORT).show()
                    binding.swipeRefresh.isRefreshing = false
                }
                is ApiResult.Progress -> binding.swipeRefresh.isRefreshing = true
                is ApiResult.Success -> binding.swipeRefresh.isRefreshing = false
            }
        }.launchIn(lifecycleScope)
    }

    @SuppressLint("SetTextI18n")
    private fun fetchNewDataOnAppStart() {
        if (!viewModel.isLoggedIn.value) {
            viewModel.fetchData()
        }

        binding.courseLoading.isVisible = true
        binding.nextCourseInfoCard.isVisible = true
        binding.nextCourseRoot.alpha = 0f

        HomeState.nextCourse.observe(viewLifecycleOwner) {
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
    }

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
        HomeState.fetchNewCurrent.tryEmit(Unit)
        HomeState.fetchNewNext.tryEmit(Unit)
    }
}