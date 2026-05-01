package hu.kocsisgeri.betterneptun.data.api

import hu.kocsisgeri.betterneptun.data.model.ApiResponseDto
import hu.kocsisgeri.betterneptun.data.model.ExtendedTermDto
import hu.kocsisgeri.betterneptun.data.model.MessageDetailsDto
import hu.kocsisgeri.betterneptun.data.model.MessageListDto
import hu.kocsisgeri.betterneptun.data.model.PostIdsRequestDto
import hu.kocsisgeri.betterneptun.data.model.SubjectDto
import hu.kocsisgeri.betterneptun.data.model.TermAveragesDto
import hu.kocsisgeri.betterneptun.data.model.TermDetailDto
import hu.kocsisgeri.betterneptun.data.model.TermDto
import hu.kocsisgeri.betterneptun.data.model.UnreadMessagesCountDto
import hu.kocsisgeri.betterneptun.data.model.UserInfoDto
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