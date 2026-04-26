package hu.kocsisgeri.betterneptun.ui.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.data.dao.ApiResult
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.utils.*
import hu.kocsisgeri.betterneptun.data.datamanager.DataManager
import hu.kocsisgeri.betterneptun.data.model.AuthenticationRequestDto
import hu.kocsisgeri.betterneptun.ui.screen.login.model.LoginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

class LoginViewModel(
    private val neptunRepository: NeptunRepository,
    private val dataManager: DataManager,
) : ViewModel() {

    private val neptunCode = MutableStateFlow<String?>(null)
    private val password = MutableStateFlow<String?>(null)
    private val stayLoggedIn = MutableStateFlow(false)
    private val forcedState = MutableStateFlow<LoginState?>(null)

    val loginState = combine(
        neptunCode,
        password,
        stayLoggedIn,
        forcedState
    ) { neptunCode, password, stayLoggedIn, forcedState ->
        forcedState?: LoginState.Idle(
            neptunCode = neptunCode.orEmpty(),
            password = password.orEmpty(),
            stayLoggedIn = stayLoggedIn,
            isButtonEnabled = neptunCode.isNullOrEmpty().not() && password.isNullOrEmpty().not()
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        initialValue = null
    )


    fun login(isSilentLogin: Boolean) {
        viewModelScope.launch {
            val user = AuthenticationRequestDto(
                neptunCode.value.orEmpty(),
                password.value.orEmpty()
            )

            forcedState.emit(
                if (isSilentLogin) {
                    LoginState.SilentLogin
                } else {
                    LoginState.Loading
                }
            )

            neptunRepository.login(user.userName, user.password).let { result ->
                when (result) {
                    is ApiResult.Error -> forcedState.emit(
                        LoginState.Error(result.error)
                    )
                    is ApiResult.Progress -> forcedState.emit(
                        if (isSilentLogin) LoginState.SilentLogin
                        else LoginState.Loading
                    )
                    is ApiResult.Success -> {
                        dataManager.putData(PREF_STAY_LOGGED_ID, stayLoggedIn.value)
                        dataManager.putData(PREF_CURRENT_USER, user)
                        neptunRepository.setStudentData(result.data)
                        forcedState.emit(LoginState.Success(result.data))
                    }
                }
            }
        }
    }

    fun passwordInput(input: String) {
        password.tryEmit(input)
    }

    fun neptunCodeInput(input: String) {
        neptunCode.tryEmit(input)
    }

    fun keepMeLoggedIn(keep: Boolean) {
        stayLoggedIn.tryEmit(keep)
    }

    fun setIdle() {
        forcedState.tryEmit(null)
    }

    init {
        dataManager.getDefault(PREF_STAY_LOGGED_ID, false).let {
            if (it) {
                try {
                    dataManager.getDefault<AuthenticationRequestDto?>(PREF_CURRENT_USER, null)?.let { user ->
                        if (user.userName.isNotBlank() && user.password.isNotBlank()) {
                            forcedState.tryEmit(LoginState.SilentLogin)
                            neptunCode.tryEmit(user.userName)
                            password.tryEmit(user.password)
                            login(true)
                        }
                    }
                } catch (ex: Exception) {
                    Timber.e(ex)
                }
            }
        }
    }
}