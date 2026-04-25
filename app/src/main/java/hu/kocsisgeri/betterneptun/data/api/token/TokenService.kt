package hu.kocsisgeri.betterneptun.data.api.token

import hu.kocsisgeri.betterneptun.data.api.network.NetworkResponse
import hu.kocsisgeri.betterneptun.data.datamanager.DataManager
import hu.kocsisgeri.betterneptun.data.datasource.NetworkDataSource
import hu.kocsisgeri.betterneptun.data.model.AuthenticationRequestDto
import hu.kocsisgeri.betterneptun.ui.model.NeptunUser
import hu.kocsisgeri.betterneptun.utils.PREF_CURRENT_USER
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class TokenService(
    private val dataManager: DataManager,
    private val networkDataSource: NetworkDataSource
) {
    private val mutex = Mutex()

    suspend fun executeApiRequestWithToken(
        onForceLogOut: () -> Unit,
        onExecuteApiRequest: suspend (token: String) -> Unit,
    ) {
        // Get the stored token
        val token = getToken()

        // Check if the token exists
        val newToken = mutex.withLock {
            val currentUser = dataManager.getDefault<NeptunUser?>(
                key = PREF_CURRENT_USER, default = null
            )

            checkNotNull(currentUser) { "No user found." }

            token ?: saveToken(
                networkDataSource.initiateLogin(
                    AuthenticationRequestDto(
                        userName = currentUser.UserLogin,
                        password = currentUser.Password
                    )
                ).let {
                    when (it) {
                        is NetworkResponse.Failure<*> -> error("No valid token found.")
                        is NetworkResponse.Success -> it.data.data.accessToken
                    }
                }
            )
        }

        try {
            // Try to execute the api request with the current token
            onExecuteApiRequest(newToken)
        } catch (ex: Exception) {
            onForceLogOut()
        }
    }

    private fun saveToken(token: String): String {
        dataManager.putData(TOKEN_KEY, token)

        return token
    }

    private fun getToken(): String? = dataManager.getDefault(TOKEN_KEY, null)

    companion object {
        private const val TOKEN_KEY = "stored_token"
    }
}