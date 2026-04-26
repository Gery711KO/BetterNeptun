package hu.kocsisgeri.betterneptun.data.datasource

import hu.kocsisgeri.betterneptun.data.api.APIService
import hu.kocsisgeri.betterneptun.data.model.AuthenticationRequestDto
import hu.kocsisgeri.betterneptun.data.model.AuthenticationResponseDto
import hu.kocsisgeri.betterneptun.data.model.UserInfoDto
import hu.kocsisgeri.betterneptun.data.api.network.CustomCookieJar
import hu.kocsisgeri.betterneptun.data.api.network.NetworkResponse
import hu.kocsisgeri.betterneptun.data.model.ApiResponseDto
import hu.kocsisgeri.betterneptun.data.model.MessageListDto
import hu.kocsisgeri.betterneptun.data.model.UnreadMessagesCountDto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NetworkDataSourceImpl(
    private val api: APIService,
    private val cookieJar: CustomCookieJar
) : NetworkDataSource {

    val coroutineContext: CoroutineDispatcher = Dispatchers.IO

    override suspend fun initiateLogin(userData: AuthenticationRequestDto) =
        withContext(coroutineContext) {
            api.authenticate(userData)
        }

    override suspend fun getUserInfo(token: String) =
        token.withToken {
            api.getUserInfo(it)
        }

    override suspend fun getUnreadMessageCount(token: String) =
        token.withToken {
            api.getUnreadMessagesCount(it)
        }

    override suspend fun getReceivedMessages(
        token: String,
        firstRow: Int,
        lastRow: Int
    ) = token.withToken {
        api.getReceivedMessages(
            token = it,
            firstRow = firstRow,
            lastRow = lastRow
        )
    }

    private suspend fun <T: Any> String.withToken(
        runnable: suspend (token: String) -> T,
    ): T = withContext(coroutineContext) {
        runnable("Bearer ${this@withToken}")
    }

    companion object {
        const val THREE_MONTHS_IN_MILLIS = 7862400000
    }
}