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

@Singleton
class MainApiService(
    @Named("MainClient") private val client: HttpClient
) {

    suspend fun getUserInfo(): ApiResponseDto<UserInfoDto> {
        return client.get("UserInfo").body()
    }

    suspend fun getUnreadMessagesCount(): ApiResponseDto<UnreadMessagesCountDto> {
        return client.get("Message/GetUnreadedMessagesCount").body()
    }

    suspend fun getReceivedMessages(
        firstRow: Int,
        lastRow: Int,
        filterType: Int = 0
    ): ApiResponseDto<MessageListDto> {
        return client.get("Message/GetReceivedMessages") {
            parameter("firstRow", firstRow)
            parameter("lastRow", lastRow)
            parameter("filterType", filterType)
        }.body()
    }

    suspend fun getUserAvatars(
        userIds: List<String>,
        type: String = "Thumbnail"
    ): ApiResponseDto<List<UserAvatarDto>> {
        return client.get("General/GetUsersAvatar") {
            // Ktor automatically repeats the key for list items: userIds=1&userIds=2
            userIds.forEach { id -> parameter("userIds", id) }
            parameter("imageSizeType", type)
        }.body()
    }

    suspend fun getMessageDetails(
        msgId: String,
        messageId: String
    ): ApiResponseDto<MessageDetailsDto> {
        return client.get("Messages/$msgId/Posts") {
            parameter("messageId", messageId)
        }.body()
    }

    suspend fun postMessagePostRead(
        messageId: String,
        postIds: PostIdsRequestDto
    ) {
        client.post("Messages/$messageId/Posts/Processed") {
            setBody<PostIdsRequestDto>(postIds)
        }
    }

    suspend fun getTermDetails(
        termId: String
    ): ApiResponseDto<TermDetailDto> {
        return client.get("Advancement/GetStudentTrainingTermData") {
            parameter("studentTrainingTermDataId", termId)
        }.body()
    }

    suspend fun getTerms(): ApiResponseDto<List<TermDto>> {
        return client.get("TakenSubjects/Terms").body()
    }

    suspend fun getTermAverages(): ApiResponseDto<TermAveragesDto> {
        return client.get("Advancement/GetTermAveragesByTraining").body()
    }

    suspend fun getTakenSubjects(
        termId: String,
        firstRow: Int = 0,
        lastRow: Int = 50,
        sort: String = "asc"
    ): ApiResponseDto<List<SubjectDto>> {
        return client.get("TakenSubjects") {
            parameter("request.termId", termId)
            parameter("sortAndPage.firstRow", firstRow)
            parameter("sortAndPage.lastRow", lastRow)
            parameter("sortAndPage.subjectName", sort)
        }.body()
    }
}
