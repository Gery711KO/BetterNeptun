package hu.kocsisgeri.betterneptun.ui.timetable

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.lifecycleScope
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener
import hu.kocsisgeri.betterneptun.data.repository.course.CourseRepo
import hu.kocsisgeri.betterneptun.databinding.FragmentTimetableBinding
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

    private val colorData = MutableStateFlow(0)

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
        setColorChooser()
        observeEventCLicks()
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

    private fun setColorChooser() {
        binding.currentColorButton.apply {
            setOnClickListener {
                viewModel.changeColor(viewModel.currentSelected.value, colorData.value)
                closeColorPicker()
            }
        }
        binding.colorPicker.setColorSelectionListener(object : SimpleColorSelectionListener() {
            override fun onColorSelected(color: Int) {
                // Do whatever you want with the color
                colorData.tryEmit(color)
                binding.currentColorButton.background.setTint(color)
            }
        })
    }

    private fun observeEventCLicks() {
        viewModel.clicked.onEach {
            openColorPicker(it)
        }.launchIn(lifecycleScope)
    }

    private fun openColorPicker(color: Int) {
        if (!binding.colorPickerCard.isVisible) {
            colorPickerAnimation(true)
        }
        colorData.tryEmit(color)
        binding.colorPicker.setColor(color)
        binding.currentColorButton.background.setTint(color)
    }

    private fun closeColorPicker() {
        colorPickerAnimation(false)
    }

    private fun colorPickerAnimation(open: Boolean) {
        if (open) {
            binding.colorPickerCard.scaleX = 00f
            binding.colorPickerCard.scaleY = 00f
            binding.colorPickerCard.animate().scaleX(1f).scaleY(1f).apply {
                duration = 200
            }.withStartAction { binding.colorPickerCard.isVisible = true }.start()
        } else {
            binding.colorPickerCard.animate().scaleX(00f).scaleY(00f).apply {
                duration = 200
            }.withEndAction { binding.colorPickerCard.isVisible = false }.start()
        }
    }
}