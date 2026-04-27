package hu.kocsisgeri.betterneptun.data.api

import hu.kocsisgeri.betterneptun.data.model.ApiResponseDto
import hu.kocsisgeri.betterneptun.data.model.MessageDetailsDto
import hu.kocsisgeri.betterneptun.data.model.MessageListDto
import hu.kocsisgeri.betterneptun.data.model.TermDetailDto
import hu.kocsisgeri.betterneptun.data.model.ExtendedTermDto
import hu.kocsisgeri.betterneptun.data.model.TermAveragesDto
import hu.kocsisgeri.betterneptun.data.model.TermDto
import hu.kocsisgeri.betterneptun.data.model.UnreadMessagesCountDto
import hu.kocsisgeri.betterneptun.data.model.UserInfoDto
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface MainApiService {

    @Headers("Content-Type: application/json")
    @GET("UserInfo")
    suspend fun getUserInfo(): ApiResponseDto<UserInfoDto>

    @Headers("Content-Type: application/json")
    @GET("Message/GetUnreadedMessagesCount")
    suspend fun getUnreadMessagesCount(): ApiResponseDto<UnreadMessagesCountDto>

    @Headers("Content-Type: application/json")
    @GET("Message/GetReceivedMessages")
    suspend fun getReceivedMessages(
        @Query("firstRow") firstRow: Int,
        @Query("lastRow") lastRow: Int,
        @Query("filterType") filterType: Int = 0
    ): ApiResponseDto<MessageListDto>

    @Headers("Content-Type: application/json")
    @GET("Messages/{msgId}/Posts")
    suspend fun getMessageDetails(
        @Path("msgId") msgId: String,
        @Query("messageId") messageId: String,
    ): ApiResponseDto<MessageDetailsDto>

    @Headers("Content-Type: application/json")
    @GET("RegistrySheet/GetStudentTrainingTermData")
    suspend fun getExtendedTerms(): ApiResponseDto<List<ExtendedTermDto>>

    @Headers("Content-Type: application/json")
    @GET("Advancement/GetStudentTrainingTermData")
    suspend fun getTermDetails(
        @Query("studentTrainingTermDataId") termId: String
    ): ApiResponseDto<TermDetailDto>

    @Headers("Content-Type: application/json")
    @GET("TakenSubjects/Terms")
    suspend fun getTerms(): ApiResponseDto<List<TermDto>>

    @Headers("Content-Type: application/json")
    @GET("Advancement/GetTermAveragesByTraining")
    suspend fun getTermAverages(): ApiResponseDto<TermAveragesDto>
}