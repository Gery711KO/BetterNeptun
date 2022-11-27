package hu.kocsisgeri.betterneptun.ui.timetable

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import hu.kocsisgeri.betterneptun.databinding.FragmentTimetableBinding
import hu.kocsisgeri.betterneptun.ui.model.CourseModel
import hu.kocsisgeri.betterneptun.ui.timetable.model.FragmentWeekViewAdapter
import hu.kocsisgeri.betterneptun.utils.observe
import hu.kocsisgeri.betterneptun.utils.setBackButton
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
        setAdapter()
        initTable()
        setBackButton(binding.backButton)
        observeEventCLicks()
        setModeButton()
    }

    private fun setAdapter() {
        adapter = FragmentWeekViewAdapter(viewModel.clickHandler)
        binding.weekView.adapter = adapter
    }

    private fun initTable() {
        viewModel.times.observe(viewLifecycleOwner) {
            binding.weekView.minHour = it.split(":")[0].toInt()
            binding.weekView.maxHour = it.split(":")[1].toInt()
        }

        viewModel.timetableEvents.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
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

        viewModel.viewMode.observe(viewLifecycleOwner) {
            binding.weekView.numberOfVisibleDays = viewModel.viewMode.value.days
            binding.modeButton.setImageDrawable(
                context?.getDrawable(viewModel.viewMode.value.icon)
            )
        }
    }

    private fun observeEventCLicks() {
        viewModel.clicked.observe(viewLifecycleOwner) {
            findNavController().navigate(
                TimetableFragmentDirections.toCourseDetail(CourseModel(it))
            )
        }
    }
}