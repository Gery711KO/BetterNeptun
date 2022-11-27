package hu.kocsisgeri.betterneptun.ui.timetable.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.text.capitalize
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener
import hu.kocsisgeri.betterneptun.R
import hu.kocsisgeri.betterneptun.databinding.FragmentCourseDetailBinding
import hu.kocsisgeri.betterneptun.ui.messages.detail_dialog.setStatusAndNavbarTransparency
import hu.kocsisgeri.betterneptun.ui.timetable.TimetableFragment
import hu.kocsisgeri.betterneptun.ui.timetable.TimetableViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.time.format.TextStyle
import java.util.*

enum class Animation {
    OPEN, CLOSE
}

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
        setBackgroundClick()
    }

    private fun setBackgroundClick() {
        binding.content.isClickable = true
        binding.background.setOnClickListener {
            closeAnimation()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setData() {
        args.model?.let {
            val day =
                it.event.startTime.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
            val startMin = it.event.startTime.minute.let { min -> if (min < 10) "0$min" else min }
            val endMin = it.event.endTime.minute.let { min -> if (min < 10) "0$min" else min }

            viewModel.selectEvent(it.event)
            binding.courseCode.text = it.event.courseCode
            binding.subjectCode.text = it.event.subjectCode
            binding.className.text = it.event.title.trim()
            binding.teacher.text = it.event.teacher
            binding.color.background.setTint(it.event.color)
            binding.room.text = it.event.location
            binding.time.text = "${it.event.startTime.hour}:${startMin} - ${it.event.endTime.hour}:${endMin}" +
                    " (${day.replaceFirstChar { char -> char.uppercase() }})"
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
        binding.animationRoot.scaleY = 0f
        binding.animationRoot.scaleX = 0f
        binding.animationRoot.animate().scaleX(1f).scaleY(1f).apply {
            duration = 200
        }.start()
    }

    private fun closeButton() {
        binding.closeButton.setOnClickListener {
            closeAnimation()
        }
    }

    private fun closeAnimation() {
        binding.animationRoot.animate().scaleX(0f).scaleY(0f).apply {
            duration = 200
        }.withEndAction { findNavController().popBackStack() }.start()
    }

    private fun setColorChooser() {
        binding.currentColorButton.apply {
            setOnClickListener {
                viewModel.changeColor(viewModel.getSelectedEvent(), colorData.value)
                viewModel.selectEvent(
                    viewModel.getSelectedEvent()?.copy(color = colorData.value)
                )
                binding.color.background.setTint(colorData.value)
                closeColorPicker()
            }
        }
        binding.colorPicker.setColorSelectionListener(object : SimpleColorSelectionListener() {
            override fun onColorSelected(color: Int) {
                colorData.tryEmit(color)
                binding.color.background.setTint(color)
                binding.currentColorButton.background.setTint(color)
            }
        })
    }

    private fun observeEventCLicks() {
        binding.color.setOnClickListener {
            viewModel.getSelectedEvent()?.let { event ->
                openColorPicker(event.color)
            }
        }
    }

    private fun openColorPicker(color: Int) {
        if (!binding.colorPickerCard.isVisible) {
            binding.color.text = "Mégsem"
            colorPickerAnimation(Animation.OPEN)
        } else {
            binding.color.text = "Átállít"
            colorPickerAnimation(Animation.CLOSE)
        }
        colorData.tryEmit(color)
        binding.colorPicker.setColor(color)
        binding.currentColorButton.background.setTint(color)
    }

    private fun closeColorPicker() {
        binding.color.text = "Átállít"
        colorPickerAnimation(Animation.CLOSE)
    }

    private fun colorPickerAnimation(animation: Animation) {
        when (animation) {
            Animation.OPEN -> {
                binding.colorPickerCard.scaleX = 0f
                binding.colorPickerCard.scaleY = 0f
                binding.colorPickerCard.animate().scaleX(1f).scaleY(1f).apply {
                    duration = 200
                }.withStartAction { binding.colorPickerCard.isVisible = true }.start()
            }
            Animation.CLOSE -> {
                binding.colorPickerCard.animate().scaleX(0f).scaleY(0f).apply {
                    duration = 200
                }.withEndAction {
                    binding.colorPickerCard.isVisible = false
                }.start()
            }
        }
    }
}