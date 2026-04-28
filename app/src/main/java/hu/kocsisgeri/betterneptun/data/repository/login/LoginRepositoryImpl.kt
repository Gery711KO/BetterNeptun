package hu.kocsisgeri.betterneptun.data.repository.login

import hu.kocsisgeri.betterneptun.data.datasource.LocalDataSource
import hu.kocsisgeri.betterneptun.data.datasource.NetworkDataSource
import hu.kocsisgeri.betterneptun.data.model.AuthenticationRequestDto
import hu.kocsisgeri.betterneptun.data.repository.runApiCall
import hu.kocsisgeri.betterneptun.domain.model.ApiResult
import hu.kocsisgeri.betterneptun.domain.model.StudentData
import hu.kocsisgeri.betterneptun.domain.repository.login.LoginRepository
import hu.kocsisgeri.betterneptun.common.PREF_CURRENT_USER
import hu.kocsisgeri.betterneptun.common.PREF_STAY_LOGGED_ID
import hu.kocsisgeri.betterneptun.common.get
import hu.kocsisgeri.betterneptun.common.put
import hu.kocsisgeri.betterneptun.data.api.token.AuthStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow

internal class LoginRepositoryImpl(
    authStore: AuthStore,
    private val localDataSource: LocalDataSource,
    private val networkDataSource: NetworkDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : LoginRepository {

    override val studentData = MutableStateFlow<ApiResult<StudentData>>(ApiResult.Loading)
    override val shouldAutoLogin = MutableSharedFlow<Boolean>(1, 1)
    override val forceLogOut: SharedFlow<Unit> = authStore.forceLogout

    init {
        shouldAutoLogin.tryEmit(
            localDataSource.cache.get(PREF_STAY_LOGGED_ID, false)
        )
    }

    override suspend fun login(neptunCode: String, password: String) {
        studentData.runApiCall(ioDispatcher) {
            networkDataSource.getUserInfo().data.let {
                StudentData(
                    name = it.name,
                    neptun = it.neptunCode
                )
            }
        }
    }

    override suspend fun silentLogin() {
        val currentUser = localDataSource.cache.get<AuthenticationRequestDto?>(
            key = PREF_CURRENT_USER,
            defaultValue = null
        )

        if (currentUser == null) studentData.value = ApiResult.Error("No current user")
        else login(
            neptunCode = currentUser.userName,
            password = currentUser.password
        )
    }

    override fun saveCurrentUser(neptunCode: String, password: String) {
        localDataSource.cache.put(
            key = PREF_CURRENT_USER,
            value = AuthenticationRequestDto(
                userName = neptunCode,
                password = password
            )
        )
    }

    override fun saveAutoLoginPreference(shouldAutoLogin: Boolean) {
        localDataSource.cache.put(
            key = PREF_STAY_LOGGED_ID,
            value = shouldAutoLogin
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun purge() {
        studentData.value = ApiResult.Loading
        shouldAutoLogin.resetReplayCache()
    }
}