package hu.kocsisgeri.betterneptun.data.datasource

import hu.kocsisgeri.betterneptun.core.network.model.UserInfoDto
import hu.kocsisgeri.betterneptun.core.network.model.ApiResponseDto
import hu.kocsisgeri.betterneptun.core.network.model.MessageDetailsDto
import hu.kocsisgeri.betterneptun.core.network.model.MessageListDto
import hu.kocsisgeri.betterneptun.core.network.model.TermDetailDto
import hu.kocsisgeri.betterneptun.core.network.model.ExtendedTermDto
import hu.kocsisgeri.betterneptun.core.network.model.PostIdsRequestDto
import hu.kocsisgeri.betterneptun.core.network.model.SubjectDto
import hu.kocsisgeri.betterneptun.core.network.model.TermAveragesDto
import hu.kocsisgeri.betterneptun.core.network.model.TermDto
import hu.kocsisgeri.betterneptun.core.network.model.UnreadMessagesCountDto
import hu.kocsisgeri.betterneptun.core.network.model.UserAvatarDto

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

    suspend fun getExtendedTerms(): ApiResponseDto<List<ExtendedTermDto>>

    suspend fun getTermDetails(termId: String): ApiResponseDto<TermDetailDto>

    suspend fun getTerms(): ApiResponseDto<List<TermDto>>

    suspend fun getTermAverages(): ApiResponseDto<TermAveragesDto>

    suspend fun getTakenSubjects(termId: String): ApiResponseDto<List<SubjectDto>>
}