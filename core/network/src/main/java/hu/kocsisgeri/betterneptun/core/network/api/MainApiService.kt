package hu.kocsisgeri.betterneptun.core.network.api

import hu.kocsisgeri.betterneptun.core.network.model.ApiResponseDto
import hu.kocsisgeri.betterneptun.core.network.model.ExtendedTermDto
import hu.kocsisgeri.betterneptun.core.network.model.MessageDetailsDto
import hu.kocsisgeri.betterneptun.core.network.model.MessageListDto
import hu.kocsisgeri.betterneptun.core.network.model.PostIdsRequestDto
import hu.kocsisgeri.betterneptun.core.network.model.SubjectDto
import hu.kocsisgeri.betterneptun.core.network.model.TermAveragesDto
import hu.kocsisgeri.betterneptun.core.network.model.TermDetailDto
import hu.kocsisgeri.betterneptun.core.network.model.TermDto
import hu.kocsisgeri.betterneptun.core.network.model.UnreadMessagesCountDto
import hu.kocsisgeri.betterneptun.core.network.model.UserAvatarDto
import hu.kocsisgeri.betterneptun.core.network.model.UserInfoDto
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

    @GET("RegistrySheet/GetStudentTrainingTermData")
    suspend fun getExtendedTerms(): ApiResponseDto<List<ExtendedTermDto>>

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