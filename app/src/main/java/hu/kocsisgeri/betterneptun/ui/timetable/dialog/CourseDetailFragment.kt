package hu.kocsisgeri.betterneptun.ui.timetable.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener
import hu.kocsisgeri.betterneptun.R
import hu.kocsisgeri.betterneptun.databinding.FragmentCourseDetailBinding
import hu.kocsisgeri.betterneptun.ui.home.HomeFragment
import hu.kocsisgeri.betterneptun.ui.main.MainActivity
import hu.kocsisgeri.betterneptun.ui.messages.detail_dialog.setStatusAndNavbarTransparency
import hu.kocsisgeri.betterneptun.ui.model.CourseModel
import hu.kocsisgeri.betterneptun.ui.timetable.TimetableFragment
import hu.kocsisgeri.betterneptun.ui.timetable.TimetableFragmentDirections
import hu.kocsisgeri.betterneptun.ui.timetable.TimetableViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.time.format.TextStyle
import java.util.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class CourseDetailFragment : DialogFragment() {

    private lateinit var binding: FragmentCourseDetailBinding
    private val args by navArgs<CourseDetailFragmentArgs>()

    private val viewModel: TimetableViewModel by viewModel()

    private val colorData = MutableStateFlow(0)

    override fun getTheme(): Int = R.style.AppDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCourseDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStatusAndNavbarTransparency()
        closeButton()
        setData()
        openAnimation()
        setColorChooser()
        observeEventCLicks()
    }

    @SuppressLint("SetTextI18n")
    private fun setData() {
        args.model?.let {
            viewModel.currentSelected.tryEmit(it.event)
            binding.courseCode.text = it.event.courseCode
            binding.subjectCode.text = it.event.subjectCode
            binding.className.text = it.event.title
            binding.teacher.text = it.event.teacher
            binding.color.background.setTint(it.event.color)
            binding.room.text = it.event.location
            binding.time.text = "${it.event.startTime.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())}\n" +
                    "${it.event.startTime.hour}:${it.event.startTime.minute} - " +
                    "${it.event.endTime.hour}:${it.event.endTime.minute}"
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(requireContext(), theme) {
            override fun onBackPressed() {
                closeAnimation()
            }
        }
    }

    private fun openAnimation() {
        binding.cardRoot.scaleX = 0.2f
        binding.cardRoot.scaleY = 0.2f
        binding.cardRoot.animate().scaleX(1f).scaleY(1f).apply {
            duration = 200
        }.start()
    }

    private fun closeButton() {
        binding.closeButton.setOnClickListener {
            closeAnimation()
        }
    }

    private fun closeAnimation() {
        binding.cardRoot.animate().scaleX(0.0f).scaleY(0.0f).apply {
            duration = 200
        }.withEndAction { findNavController().popBackStack() }.start()
    }

    private fun setColorChooser() {
        binding.currentColorButton.apply {
            setOnClickListener {
                viewModel.changeColor(viewModel.currentSelected.value, colorData.value)
                binding.color.background.setTint(colorData.value)
                closeColorPicker()
            }
        }
        binding.colorPicker.setColorSelectionListener(object : SimpleColorSelectionListener() {
            override fun onColorSelected(color: Int) {
                colorData.tryEmit(color)
                binding.currentColorButton.background.setTint(color)
            }
        })
    }

    private fun observeEventCLicks() {
        binding.colorRow.setOnClickListener {
            viewModel.currentSelected.value?.let { event ->
                openColorPicker(event.color)
            }
        }
    }

    private fun openColorPicker(color: Int) {
        if (!binding.colorPickerCard.isVisible) {
            colorPickerAnimation(true)
        } else {
            colorPickerAnimation(false)
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
            }.withEndAction {
                getCalendarRef()?.refreshTable()
                binding.colorPickerCard.isVisible = false
            }.start()
        }
    }

    private fun getCalendarRef() : TimetableFragment? {
        return  (parentFragmentManager.fragments.firstOrNull {
            it is TimetableFragment
        }?.let { it as TimetableFragment })
    }
}