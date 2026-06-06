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
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MainApiService {

    @GET("UserInfo")
    suspend fun getUserInfo(): ApiResponseDto<UserInfoDto>

    @GET("Message/GetUnreadedMessagesCount")
    suspend fun getUnreadMessagesCount(): ApiResponseDto<UnreadMessagesCountDto>

    @GET("Message/GetReceivedMessages")
    suspend fun getReceivedMessages(
        @Query("firstRow") firstRow: Int,
        @Query("lastRow") lastRow: Int,
        @Query("filterType") filterType: Int = 0
    ): ApiResponseDto<MessageListDto>

    @GET("General/GetUsersAvatar")
    suspend fun getUserAvatars(
        @Query("userIds") userIds: List<String>,
        @Query("imageSizeType") type: String = "Thumbnail"
    ): ApiResponseDto<List<UserAvatarDto>>

    @GET("Messages/{msgId}/Posts")
    suspend fun getMessageDetails(
        @Path("msgId") msgId: String,
        @Query("messageId") messageId: String,
    ): ApiResponseDto<MessageDetailsDto>

    @POST("Messages/{messageId}/Posts/Processed")
    suspend fun postMessagePostRead(
        @Path("messageId") messageId: String,
        @Body postIds: PostIdsRequestDto
    )

    @GET("Advancement/GetStudentTrainingTermData")
    suspend fun getTermDetails(
        @Query("studentTrainingTermDataId") termId: String
    ): ApiResponseDto<TermDetailDto>

    @GET("TakenSubjects/Terms")
    suspend fun getTerms(): ApiResponseDto<List<TermDto>>

    @GET("Advancement/GetTermAveragesByTraining")
    suspend fun getTermAverages(): ApiResponseDto<TermAveragesDto>

    @GET("TakenSubjects")
    suspend fun getTakenSubjects(
        @Query("request.termId") termId: String,
        @Query("sortAndPage.firstRow") firstRow: Int = 0,
        @Query("sortAndPage.lastRow") lastRow: Int = 50,
        @Query("sortAndPage.subjectName") sort: String = "asc",
    ): ApiResponseDto<List<SubjectDto>>
}
