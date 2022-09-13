package hu.kocsisgeri.betterneptun.ui.settings

import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import android.widget.RadioButton
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import hu.kocsisgeri.betterneptun.R
import hu.kocsisgeri.betterneptun.data.repository.course.CourseRepo
import hu.kocsisgeri.betterneptun.databinding.FragmentSettingsBinding
import hu.kocsisgeri.betterneptun.utils.ThemeMode
import hu.kocsisgeri.betterneptun.utils.getCurrentTheme
import hu.kocsisgeri.betterneptun.utils.setBackButton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModel()
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackButton(binding.backButton)
        setThemeSelection()
        handleThemeSelect()
        setExitButton()
        setStartTimes()
    }

    private fun setStartTimes() {
        CourseRepo.isTimelineAutomatic.asLiveData().observe(viewLifecycleOwner) { auto ->

            CourseRepo.firstClassTime.asLiveData().observe(viewLifecycleOwner) {
                binding.firstClassTime.text = it
            }

            CourseRepo.lastClassTime.asLiveData().observe(viewLifecycleOwner) {
                binding.lastClassTime.text = it
            }

            if (auto == true) {
                CourseRepo.courses.value.map {
                    it.startTime.hour
                }.min().let {
                    CourseRepo.firstClassTime.tryEmit("$it:00")
                }

                CourseRepo.courses.value.map {
                    it.endTime.hour
                }.max().let {
                    CourseRepo.lastClassTime.tryEmit("${it + 1}:00")
                }
            }
        }
    }

    private fun setExitButton() {
        binding.logoutCard.setOnClickListener {
            viewModel.logout()
            findNavController().navigate(SettingsFragmentDirections.toLogin())
        }
    }

    private fun setThemeSelection() {
        viewModel.themeMode.onEach { theme ->
            theme?.let {
                when (it) {
                    ThemeMode.AUTO -> binding.themeAuto.select()
                    ThemeMode.DARK -> binding.themeDark.select()
                    ThemeMode.LIGHT -> binding.themeLight.select()
                }
            }
        }.launchIn(lifecycleScope)
    }

    private fun handleThemeSelect() {
        binding.themeGroup.setOnCheckedChangeListener { radioGroup, i ->
            when (radioGroup.checkedRadioButtonId) {
                R.id.theme_dark -> binding.themeDark.select()
                R.id.theme_light -> binding.themeLight.select()
                R.id.theme_auto -> binding.themeAuto.select()
            }
        }
    }

    private fun RadioButton.select() {
        isChecked = true
        alpha = 1f

        when (id) {
            R.id.theme_dark -> {
                ThemeMode.DARK.let {
                    viewModel.saveTheme(it)
                    binding.themeLight.unselect()
                    binding.themeAuto.unselect()
                    if (requireContext().getCurrentTheme() != it)
                        AppCompatDelegate.setDefaultNightMode(it.mode)
                }
            }
            R.id.theme_light -> {
                ThemeMode.LIGHT.let {
                    viewModel.saveTheme(it)
                    binding.themeDark.unselect()
                    binding.themeAuto.unselect()
                    if (requireContext().getCurrentTheme() != it)
                        AppCompatDelegate.setDefaultNightMode(it.mode)
                }
            }
            R.id.theme_auto -> {
                ThemeMode.AUTO.let {
                    viewModel.saveTheme(it)
                    binding.themeDark.unselect()
                    binding.themeLight.unselect()
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
            }
        }
    }

    private fun RadioButton.unselect() {
        isChecked = false
        alpha = 0.5f
    }
}