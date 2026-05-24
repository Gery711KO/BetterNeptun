package hu.kocsisgeri.betterneptun.data.datasource

import hu.kocsisgeri.betterneptun.core.network.model.neptun.UserInfoDto
import hu.kocsisgeri.betterneptun.core.network.model.neptun.ApiResponseDto
import hu.kocsisgeri.betterneptun.core.network.model.neptun.MessageDetailsDto
import hu.kocsisgeri.betterneptun.core.network.model.neptun.MessageListDto
import hu.kocsisgeri.betterneptun.core.network.model.neptun.TermDetailDto
import hu.kocsisgeri.betterneptun.core.network.model.neptun.PostIdsRequestDto
import hu.kocsisgeri.betterneptun.core.network.model.neptun.SubjectDto
import hu.kocsisgeri.betterneptun.core.network.model.neptun.TermAveragesDto
import hu.kocsisgeri.betterneptun.core.network.model.neptun.TermDto
import hu.kocsisgeri.betterneptun.core.network.model.neptun.UnreadMessagesCountDto
import hu.kocsisgeri.betterneptun.core.network.model.neptun.UserAvatarDto

internal interface NetworkDataSource {

    suspend fun getUserInfo(): ApiResponseDto<UserInfoDto>

    suspend fun getUnreadMessageCount(): ApiResponseDto<UnreadMessagesCountDto>

    suspend fun getReceivedMessages(
        firstRow: Int,
        lastRow: Int,
    ): ApiResponseDto<MessageListDto>

    suspend fun getUserAvatars(
        userIds: List<String>,
    ): ApiResponseDto<List<UserAvatarDto>>

    suspend fun getMessageDetails(
        messageId: String,
    ): ApiResponseDto<MessageDetailsDto>

    suspend fun postMessagePostRead(
        messageId: String,
        postIds: PostIdsRequestDto
    )

    suspend fun getTermDetails(termId: String): ApiResponseDto<TermDetailDto>

    suspend fun getTerms(): ApiResponseDto<List<TermDto>>

    suspend fun getTermAverages(): ApiResponseDto<TermAveragesDto>

    suspend fun getTakenSubjects(termId: String): ApiResponseDto<List<SubjectDto>>
}
