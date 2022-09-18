package hu.kocsisgeri.betterneptun.ui.timetable

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import hu.kocsisgeri.betterneptun.databinding.FragmentTimetableBinding
import hu.kocsisgeri.betterneptun.ui.model.CourseModel
import hu.kocsisgeri.betterneptun.ui.timetable.model.FragmentWeekViewAdapter
import hu.kocsisgeri.betterneptun.utils.setBackButton
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
        viewModel.times.distinctUntilChanged().observe(viewLifecycleOwner, Observer {
            binding.weekView.minHour = it.split(":")[0].toInt()
            binding.weekView.maxHour = it.split(":")[1].toInt()
            adapter =
                FragmentWeekViewAdapter(
                    loadMoreHandler = viewModel::addEvents,
                    viewModel.clickHandler
                )
            binding.weekView.adapter = adapter
            viewModel.eventList.distinctUntilChanged()
                .observe(viewLifecycleOwner, Observer { list ->
                    adapter.submitList(list)
                    adapter.refresh()
                })
        })
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