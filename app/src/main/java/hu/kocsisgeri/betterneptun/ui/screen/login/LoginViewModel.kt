package hu.kocsisgeri.betterneptun.ui.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.data.datasource.LocalDataSource
import hu.kocsisgeri.betterneptun.data.model.AuthenticationRequestDto
import hu.kocsisgeri.betterneptun.domain.model.ApiResult
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.ui.screen.login.model.LoginState
import hu.kocsisgeri.betterneptun.utils.PREF_CURRENT_USER
import hu.kocsisgeri.betterneptun.utils.PREF_STAY_LOGGED_ID
import hu.kocsisgeri.betterneptun.utils.get
import hu.kocsisgeri.betterneptun.utils.launchReportingErrors
import hu.kocsisgeri.betterneptun.utils.put
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber

class LoginViewModel(
    private val neptunRepository: NeptunRepository,
    private val localDataSource: LocalDataSource,
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
        forcedState ?: LoginState.Idle(
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
        viewModelScope.launchReportingErrors {
            val user = AuthenticationRequestDto(
                neptunCode.value.orEmpty(),
                password.value.orEmpty()
            )

            localDataSource.cache.put(PREF_CURRENT_USER, user)

            forcedState.emit(
                if (isSilentLogin) {
                    LoginState.SilentLogin
                } else {
                    LoginState.Loading
                }
            )

            neptunRepository.login(user.userName, user.password)
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
        localDataSource.cache.put(PREF_STAY_LOGGED_ID, keep)
    }

    fun setIdle() {
        forcedState.tryEmit(null)
    }

    init {
        viewModelScope.launchReportingErrors {
            neptunRepository.studentData.collect { result ->
                when (result) {
                    is ApiResult.Error -> forcedState.emit(
                        LoginState.Error(result.error)
                    )

                    is ApiResult.Loading -> {
                        // No nothing
                    }

                    is ApiResult.Success -> forcedState.emit(
                        LoginState.Success(result.data)
                    )
                }
            }
        }

        localDataSource.cache.get(PREF_STAY_LOGGED_ID, false).let {
            if (it) {
                try {
                    localDataSource.cache.get<AuthenticationRequestDto?>(
                        key = PREF_CURRENT_USER,
                        defaultValue = null,
                    )?.let { user ->
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