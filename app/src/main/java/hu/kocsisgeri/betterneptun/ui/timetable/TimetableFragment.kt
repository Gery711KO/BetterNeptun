package hu.kocsisgeri.betterneptun.ui.timetable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import hu.kocsisgeri.betterneptun.databinding.FragmentTimetableBinding
import hu.kocsisgeri.betterneptun.ui.timetable.model.FragmentWeekViewAdapter
import hu.kocsisgeri.betterneptun.utils.setBackButton
import org.koin.androidx.viewmodel.ext.android.viewModel

class TimetableFragment : Fragment() {

    private val viewModel: TimetableViewModel by viewModel()
    private lateinit var binding : FragmentTimetableBinding

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
    }

    private fun initTable() {
        val adapter = FragmentWeekViewAdapter(loadMoreHandler = viewModel::addEvents)
        binding.weekView.adapter = adapter
        viewModel.eventList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

}