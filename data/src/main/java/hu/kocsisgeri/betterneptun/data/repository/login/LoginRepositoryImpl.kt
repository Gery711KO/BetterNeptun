package hu.kocsisgeri.betterneptun.data.repository.login

import hu.kocsisgeri.betterneptun.core.network.model.neptun.AuthenticationRequestDto
import hu.kocsisgeri.betterneptun.data.datasource.LocalCacheKeys
import hu.kocsisgeri.betterneptun.data.datasource.LocalDataSource
import hu.kocsisgeri.betterneptun.data.datasource.NetworkDataSource
import hu.kocsisgeri.betterneptun.data.mapper.toAvatarDomain
import hu.kocsisgeri.betterneptun.data.util.runApiCall
import hu.kocsisgeri.betterneptun.domain.clearable.BaseClearable
import hu.kocsisgeri.betterneptun.domain.model.neptun.ApiResult
import hu.kocsisgeri.betterneptun.domain.model.neptun.StudentData
import hu.kocsisgeri.betterneptun.domain.repository.login.LoginRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.serializer
import org.koin.core.annotation.Singleton

@Singleton
class LoginRepositoryImpl internal constructor(
    private val localDataSource: LocalDataSource,
    private val networkDataSource: NetworkDataSource,
) : LoginRepository, BaseClearable() {

    override val studentData = clearableStateFlow<StudentData?>(null)
    override val shouldAutoLogin = clearableSharedFlow<Boolean>(1, 1)

    init {
        shouldAutoLogin.tryEmit(
            localDataSource.getFromSharedPreferences(
                key = LocalCacheKeys.STAY_LOGGED_ID,
                defaultValue = false,
                serializer = serializer()
            )
        )
    }

    override fun login(neptunCode: String, password: String) = runApiCall(
        onResult = { data ->
            studentData.value = data
        },
        block = {
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
}
