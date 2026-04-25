package hu.kocsisgeri.betterneptun.data.datasource

import hu.kocsisgeri.betterneptun.data.model.AuthenticationRequestDto
import hu.kocsisgeri.betterneptun.data.model.AuthenticationResponseDto
import hu.kocsisgeri.betterneptun.data.model.UserInfoDto
import hu.kocsisgeri.betterneptun.data.api.network.NetworkResponse
import hu.kocsisgeri.betterneptun.data.model.ApiResponseDto
import hu.kocsisgeri.betterneptun.data.model.MessageListDto
import hu.kocsisgeri.betterneptun.data.model.UnreadMessagesCountDto

interface NetworkDataSource {
    suspend fun initiateLogin(
        userData: AuthenticationRequestDto
    ): NetworkResponse<ApiResponseDto<AuthenticationResponseDto>, String>

    suspend fun getUserInfo(
        token: String
    ): NetworkResponse<ApiResponseDto<UserInfoDto>, String>

    suspend fun getUnreadMessageCount(
        token: String
    ): NetworkResponse<ApiResponseDto<UnreadMessagesCountDto>, String>

    suspend fun getReceivedMessages(
        token: String,
        firstRow: Int,
        lastRow: Int,
    ): NetworkResponse<ApiResponseDto<MessageListDto>, String>
}