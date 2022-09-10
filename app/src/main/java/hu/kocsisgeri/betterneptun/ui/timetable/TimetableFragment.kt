package hu.kocsisgeri.betterneptun.ui.timetable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.distinctUntilChanged
import androidx.navigation.fragment.findNavController
import hu.kocsisgeri.betterneptun.data.repository.course.CourseRepo
import hu.kocsisgeri.betterneptun.databinding.FragmentTimetableBinding
import hu.kocsisgeri.betterneptun.ui.timetable.model.FragmentWeekViewAdapter
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
        initTable()
        setBackButton(binding.backButton)
        setRandomColorButton()
    }

    private fun initTable() {
        adapter = FragmentWeekViewAdapter(loadMoreHandler = viewModel::addEvents)
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

    private fun setRandomColorButton() {
        binding.newColors.setOnClickListener {
            viewModel.randomizeColors()
        }
    }
}