package hu.kocsisgeri.betterneptun.ui.settings

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import hu.kocsisgeri.betterneptun.BuildConfig
import hu.kocsisgeri.betterneptun.R
import hu.kocsisgeri.betterneptun.data.repository.course.HomeState
import hu.kocsisgeri.betterneptun.databinding.FragmentSettingsBinding
import hu.kocsisgeri.betterneptun.utils.ThemeMode
import hu.kocsisgeri.betterneptun.utils.getCurrentTheme
import hu.kocsisgeri.betterneptun.utils.setBackButton
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

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
        setVersionData()
    }

    @SuppressLint("SetTextI18n")
    private fun setVersionData() {
        binding.versionValue.text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
    }

    private fun setStartTimes() {
        HomeState.firstClassTime.asLiveData().observe(viewLifecycleOwner) {
            binding.firstClassTime.text = it
        }

        HomeState.lastClassTime.asLiveData().observe(viewLifecycleOwner) {
            binding.lastClassTime.text = it
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