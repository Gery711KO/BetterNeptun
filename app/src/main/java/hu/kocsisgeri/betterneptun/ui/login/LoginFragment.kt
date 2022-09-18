package hu.kocsisgeri.betterneptun.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import hu.kocsisgeri.betterneptun.databinding.FragmentLoginBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModel()

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeUserInput()
        setButton()
        setCheckBox()
        observeLoginResult()
    }

    private fun setButton() {
        viewModel.isButtonEnabled.asLiveData().observe(viewLifecycleOwner) { enabled ->
            binding.loginButton.isEnabled = enabled
            if (enabled) binding.loginButton.alpha = 1f
            else binding.loginButton.alpha = 0.6f
        }

        binding.loginButton.setOnClickListener {
            viewModel.login()
        }
    }

    private fun setCheckBox() {
        binding.enableDataSave.setOnCheckedChangeListener { _, isChecked ->
            viewModel.keepMeLoggedIn(isChecked)
        }
    }

    private fun observeLoginResult() {
        viewModel.loading.onEach {
            binding.loadingIndicator.isVisible = it
            binding.loginContent.isVisible = false
        }.launchIn(lifecycleScope)

        viewModel.hideLogin.onEach {
            binding.loginContent.isVisible = !it
        }.launchIn(lifecycleScope)

        viewModel.isSucceeded.onEach {
            binding.loadingIndicator.isVisible = false
            viewModel.clearLogin()
            viewModel.setUserData(it)
            findNavController().navigate(LoginFragmentDirections.toHomeScreen())
        }.launchIn(lifecycleScope)

        viewModel.isFailed.onEach {
            binding.loadingIndicator.isVisible = false
            binding.loginContent.isVisible = true
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }.launchIn(lifecycleScope)
    }

    private fun observeUserInput() {
        binding.loginUsernameInput.addTextChangedListener {
            viewModel.neptunCodeInput(it.toString())
        }

        binding.loginPasswordInput.addTextChangedListener {
            viewModel.passwordInput(it.toString())
        }
    }
}