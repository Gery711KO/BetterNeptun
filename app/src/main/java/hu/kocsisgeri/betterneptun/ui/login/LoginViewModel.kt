package hu.kocsisgeri.betterneptun.ui.login

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.data.dao.ApiResult
import hu.kocsisgeri.betterneptun.data.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.domain.api.datasource.NetworkDataSource
import hu.kocsisgeri.betterneptun.domain.api.network.NetworkResponse
import hu.kocsisgeri.betterneptun.domain.model.StudentData
import hu.kocsisgeri.betterneptun.ui.model.NeptunUser
import hu.kocsisgeri.betterneptun.utils.*
import hu.kocsisgeri.betterneptun.utils.data_manager.DataManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class LoginViewModel(
    private val networkDataSource: NetworkDataSource,
    private val neptunRepository: NeptunRepository,
) : ViewModel(), KoinComponent {

    private val dataManager: DataManager by inject()
    private val sharedPreferences: SharedPreferences by inject()

    private val neptunCode = MutableStateFlow<String?>(null)
    private val password = MutableStateFlow<String?>(null)
    private val stayLoggedIn = MutableStateFlow(false)

    val isButtonEnabled = combine(neptunCode, password) { neptun, pw ->
        !neptun.isNullOrBlank() && !pw.isNullOrBlank()
    }

    val isSucceeded = MutableSharedFlow<StudentData>(1, 50)
    val isFailed = MutableSharedFlow<String>(1, 50)
    val loading = MutableSharedFlow<Boolean>(1, 50)
    val hideLogin = MutableSharedFlow<Boolean>(1, 50)


    private val neptunUser = MutableStateFlow(NeptunUser())

    fun login() {
        loading.tryEmit(true)
        neptunUser.tryEmit(NeptunUser(neptunCode.value, password.value))
        viewModelScope.launch {
            networkDataSource.initiateLogin(neptunUser.value.loginRequestData()).let {
                when (it) {
                    is NetworkResponse.Success -> {
                        if (!it.data.isSuccess()) isFailed.tryEmit(it.data.getError())
                        else {
                            when (val result = networkDataSource.getData()) {
                                is ApiResult.Error -> isFailed.tryEmit(result.error)
                                is ApiResult.Progress -> {}
                                is ApiResult.Success -> {
                                    sharedPreferences.put(PREF_STAY_LOGGED_ID, stayLoggedIn.value)
                                    dataManager.putData(PREF_CURRENT_USER, neptunUser.value)
                                    neptunRepository.currentUser.tryEmit(neptunUser.value)
                                    isSucceeded.tryEmit(result.data)
                                }
                            }
                        }
                    }
                    is NetworkResponse.Failure<*> -> {
                        isFailed.tryEmit(it.error.getErrorMessage())
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

    fun clearLogin() {
        neptunCode.tryEmit(null)
        password.tryEmit(null)
        stayLoggedIn.tryEmit(false)
        neptunUser.tryEmit(NeptunUser())
    }

    init {
        sharedPreferences.get(PREF_STAY_LOGGED_ID, false).let {
            if (it) {
                try {
                    dataManager.getData(PREF_CURRENT_USER, NeptunUser::class.java)?.let { user ->
                        if (user.UserLogin != null) {
                            hideLogin.tryEmit(true)
                            neptunCode.tryEmit(user.UserLogin)
                            password.tryEmit(user.Password)
                            stayLoggedIn.tryEmit(it)
                            login()
                        }
                    }
                } catch (ex: Exception) {
                    Timber.e(ex)
                }
            }
        }
    }
}