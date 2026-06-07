package hu.kocsisgeri.betterneptun.core.network.api

import hu.kocsisgeri.betterneptun.core.network.model.neptun.ApiResponseDto
import hu.kocsisgeri.betterneptun.core.network.model.neptun.MessageDetailsDto
import hu.kocsisgeri.betterneptun.core.network.model.neptun.MessageListDto
import hu.kocsisgeri.betterneptun.core.network.model.neptun.PostIdsRequestDto
import hu.kocsisgeri.betterneptun.core.network.model.neptun.SubjectDto
import hu.kocsisgeri.betterneptun.core.network.model.neptun.TermAveragesDto
import hu.kocsisgeri.betterneptun.core.network.model.neptun.TermDetailDto
import hu.kocsisgeri.betterneptun.core.network.model.neptun.TermDto
import hu.kocsisgeri.betterneptun.core.network.model.neptun.UnreadMessagesCountDto
import hu.kocsisgeri.betterneptun.core.network.model.neptun.UserAvatarDto
import hu.kocsisgeri.betterneptun.core.network.model.neptun.UserInfoDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import org.koin.core.annotation.Named
import org.koin.core.annotation.Singleton

interface MainApiService {

    suspend fun getUserInfo(): ApiResponseDto<UserInfoDto>
    suspend fun getUnreadMessagesCount(): ApiResponseDto<UnreadMessagesCountDto>

    suspend fun getReceivedMessages(
        firstRow: Int,
        lastRow: Int,
        filterType: Int = 0
    ): ApiResponseDto<MessageListDto>

    suspend fun getUserAvatars(
        userIds: List<String>,
        type: String = "Thumbnail"
    ): ApiResponseDto<List<UserAvatarDto>>

    suspend fun getMessageDetails(msgId: String, messageId: String): ApiResponseDto<MessageDetailsDto>
    suspend fun postMessagePostRead(messageId: String, postIds: PostIdsRequestDto)
    suspend fun getTermDetails(termId: String): ApiResponseDto<TermDetailDto>
    suspend fun getTerms(): ApiResponseDto<List<TermDto>>
    suspend fun getTermAverages(): ApiResponseDto<TermAveragesDto>

    suspend fun getTakenSubjects(
        termId: String,
        firstRow: Int = 0,
        lastRow: Int = 50,
        sort: String = "asc"
    ): ApiResponseDto<List<SubjectDto>>
}
