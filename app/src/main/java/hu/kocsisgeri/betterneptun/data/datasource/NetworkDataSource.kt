package hu.kocsisgeri.betterneptun.data.datasource

import hu.kocsisgeri.betterneptun.data.model.UserInfoDto
import hu.kocsisgeri.betterneptun.data.model.ApiResponseDto
import hu.kocsisgeri.betterneptun.data.model.MessageDetailsDto
import hu.kocsisgeri.betterneptun.data.model.MessageListDto
import hu.kocsisgeri.betterneptun.data.model.UnreadMessagesCountDto

interface NetworkDataSource {

    suspend fun getUserInfo(): ApiResponseDto<UserInfoDto>

    suspend fun getUnreadMessageCount(): ApiResponseDto<UnreadMessagesCountDto>

    suspend fun getReceivedMessages(
        firstRow: Int,
        lastRow: Int,
    ): ApiResponseDto<MessageListDto>

    suspend fun getMessageDetails(
        messageId: String,
    ): ApiResponseDto<MessageDetailsDto>
}