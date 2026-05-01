package hu.kocsisgeri.betterneptun.data.datasource

import hu.kocsisgeri.betterneptun.data.model.UserInfoDto
import hu.kocsisgeri.betterneptun.data.model.ApiResponseDto
import hu.kocsisgeri.betterneptun.data.model.MessageDetailsDto
import hu.kocsisgeri.betterneptun.data.model.MessageListDto
import hu.kocsisgeri.betterneptun.data.model.TermDetailDto
import hu.kocsisgeri.betterneptun.data.model.ExtendedTermDto
import hu.kocsisgeri.betterneptun.data.model.PostIdsRequestDto
import hu.kocsisgeri.betterneptun.data.model.SubjectDto
import hu.kocsisgeri.betterneptun.data.model.TermAveragesDto
import hu.kocsisgeri.betterneptun.data.model.TermDto
import hu.kocsisgeri.betterneptun.data.model.UnreadMessagesCountDto

internal interface NetworkDataSource {

    suspend fun getUserInfo(): ApiResponseDto<UserInfoDto>

    suspend fun getUnreadMessageCount(): ApiResponseDto<UnreadMessagesCountDto>

    suspend fun getReceivedMessages(
        firstRow: Int,
        lastRow: Int,
    ): ApiResponseDto<MessageListDto>

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