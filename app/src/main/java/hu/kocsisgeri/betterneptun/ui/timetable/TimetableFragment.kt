package hu.kocsisgeri.betterneptun.ui.timetable

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener
import hu.kocsisgeri.betterneptun.data.repository.course.CourseRepo
import hu.kocsisgeri.betterneptun.databinding.FragmentTimetableBinding
import hu.kocsisgeri.betterneptun.ui.model.CourseModel
import hu.kocsisgeri.betterneptun.ui.timetable.model.FragmentWeekViewAdapter
import hu.kocsisgeri.betterneptun.utils.setBackButton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel


class TimetableFragment : Fragment() {

    private val viewModel: TimetableViewModel by viewModel()
    private lateinit var binding: FragmentTimetableBinding
    private lateinit var adapter: FragmentWeekViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTimetableBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTable()
        setBackButton(binding.backButton)
        observeEventCLicks()
        setModeButton()
    }

    private fun initTable() {
        adapter =
            FragmentWeekViewAdapter(loadMoreHandler = viewModel::addEvents, viewModel.clickHandler)
        binding.weekView.maxHour =
            CourseRepo.lastClassTime.value?.split(":")?.get(0)?.toInt() ?: 8
        binding.weekView.minHour =
            CourseRepo.firstClassTime.value?.split(":")?.get(0)?.toInt() ?: 22
        binding.weekView.adapter = adapter
        viewModel.eventList.distinctUntilChanged().observe(viewLifecycleOwner) {
            adapter.submitList(it)
            adapter.refresh()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setModeButton() {
        binding.modeButton.setOnClickListener {
            when (viewModel.viewMode.value) {
                ViewMode.WEEK -> viewModel.viewMode.tryEmit(ViewMode.DAY)
                ViewMode.DAY -> viewModel.viewMode.tryEmit(ViewMode.WEEK)
            }
        }

        viewModel.viewMode.asLiveData().observe(viewLifecycleOwner, Observer {
            binding.weekView.numberOfVisibleDays = viewModel.viewMode.value.days
            binding.modeButton.setImageDrawable(requireContext().getDrawable(viewModel.viewMode.value.icon))
        })
    }

    private fun observeEventCLicks() {
        viewModel.clicked.onEach {
            findNavController().navigate(TimetableFragmentDirections.toCourseDetail(CourseModel(it)))
        }.launchIn(lifecycleScope)
    }

    fun refreshTable() {
        viewModel.eventList.value?.let {
            adapter.submitList(it)
            adapter.refresh()
        }
    }
}