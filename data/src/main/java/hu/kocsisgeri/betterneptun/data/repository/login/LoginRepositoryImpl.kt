package hu.kocsisgeri.betterneptun.data.repository.login

import hu.kocsisgeri.betterneptun.data.datasource.LocalDataSource
import hu.kocsisgeri.betterneptun.data.datasource.NetworkDataSource
import hu.kocsisgeri.betterneptun.core.network.model.AuthenticationRequestDto
import hu.kocsisgeri.betterneptun.data.datasource.LocalCacheKeys
import hu.kocsisgeri.betterneptun.data.util.runApiCall
import hu.kocsisgeri.betterneptun.domain.model.ApiResult
import hu.kocsisgeri.betterneptun.domain.model.StudentData
import hu.kocsisgeri.betterneptun.domain.repository.login.LoginRepository
import hu.kocsisgeri.betterneptun.data.mapper.toAvatarDomain
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.serializer

internal class LoginRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val networkDataSource: NetworkDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : LoginRepository {

    override val studentData = MutableStateFlow<ApiResult<StudentData>>(ApiResult.Loading)
    override val shouldAutoLogin = MutableSharedFlow<Boolean>(1, 1)

    init {
        shouldAutoLogin.tryEmit(
            localDataSource.getFromSharedPreferences(
                key = LocalCacheKeys.STAY_LOGGED_ID,
                defaultValue = false,
                serializer = serializer()
            )
        )
    }

    override suspend fun login(neptunCode: String, password: String) {
        studentData.runApiCall(ioDispatcher) {
            networkDataSource.getUserInfo().data.let {
                StudentData(
                    name = it.name,
                    neptun = it.neptunCode,
                    avatar = it.userAvatar.toAvatarDomain()
                )
            }
        }
    }

    override suspend fun silentLogin() {
        val currentUser = localDataSource.getFromSharedPreferences<AuthenticationRequestDto?>(
            key = LocalCacheKeys.CURRENT_USER,
            defaultValue = null,
            serializer = serializer()
        )

        if (currentUser == null) studentData.value = ApiResult.Error("No current user")
        else login(
            neptunCode = currentUser.userName,
            password = currentUser.password
        )
    }

    override fun saveCurrentUser(neptunCode: String, password: String) {
        localDataSource.saveToSharedPreferences(
            key = LocalCacheKeys.CURRENT_USER,
            value = AuthenticationRequestDto(
                userName = neptunCode,
                password = password
            ),
            serializer = serializer()
        )
    }

    override fun saveAutoLoginPreference(shouldAutoLogin: Boolean) {
        localDataSource.saveToSharedPreferences(
            key = LocalCacheKeys.STAY_LOGGED_ID,
            value = shouldAutoLogin,
            serializer = serializer()
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun purge() {
        studentData.value = ApiResult.Loading
        shouldAutoLogin.resetReplayCache()
    }
}