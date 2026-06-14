package hu.kocsisgeri.betterneptun.data.repository.login

import hu.kocsisgeri.betterneptun.core.network.model.neptun.AuthenticationRequestDto
import hu.kocsisgeri.betterneptun.data.datasource.LocalCacheKeys
import hu.kocsisgeri.betterneptun.data.datasource.LocalDataSource
import hu.kocsisgeri.betterneptun.data.datasource.NetworkDataSource
import hu.kocsisgeri.betterneptun.data.mapper.toAvatarDomain
import hu.kocsisgeri.betterneptun.data.util.runApiCall
import hu.kocsisgeri.betterneptun.domain.clearable.BaseClearable
import hu.kocsisgeri.betterneptun.domain.model.ApiResult
import hu.kocsisgeri.betterneptun.domain.model.neptun.StudentData
import hu.kocsisgeri.betterneptun.domain.repository.login.LoginRepository
import hu.kocsisgeri.betterneptun.domain.token.TokenManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.serializer
import org.koin.core.annotation.Singleton

@Singleton
internal class LoginRepositoryImpl internal constructor(
    private val localDataSource: LocalDataSource,
    private val networkDataSource: NetworkDataSource,
    private val tokenManager: TokenManager,
) : LoginRepository, BaseClearable() {

    override val studentData = clearableStateFlow<StudentData?>(null)
    override val shouldAutoLogin = clearableSharedFlow<Boolean>(1, 1)

    override fun login(neptunCode: String, password: String) = runApiCall(
        onResult = { data ->
            studentData.value = data
        },
        block = {
            networkDataSource.getUserToken(
                AuthenticationRequestDto(
                    userName = neptunCode,
                    password = password
                )
            ).run {
                tokenManager.saveToken(data.accessToken)
            }

            networkDataSource.getUserInfo().data.let {
                StudentData(
                    name = it.name,
                    neptun = it.neptunCode,
                    avatar = it.userAvatar.toAvatarDomain()
                )
            }
        }
    )

    override fun silentLogin(): Flow<ApiResult<StudentData>> {
        shouldAutoLogin.tryEmit(
            localDataSource.getFromSharedPreferences(
                key = LocalCacheKeys.STAY_LOGGED_ID,
                defaultValue = false,
                serializer = serializer()
            )
        )
        val currentUser = localDataSource.getFromSharedPreferences<AuthenticationRequestDto?>(
            key = LocalCacheKeys.CURRENT_USER,
            defaultValue = null,
            serializer = serializer()
        )
        return if (currentUser == null) flowOf(ApiResult.Error("No current user"))
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

    override fun checkIfAutoLoginPossibly() {
        shouldAutoLogin.tryEmit(
            localDataSource.getFromSharedPreferences(
                key = LocalCacheKeys.STAY_LOGGED_ID,
                defaultValue = false,
                serializer = serializer()
            )
        )
    }
}
