package hu.kocsisgeri.betterneptun.data.api

import hu.kocsisgeri.betterneptun.data.model.ApiResponseDto
import hu.kocsisgeri.betterneptun.data.model.MessageListDto
import hu.kocsisgeri.betterneptun.data.model.UnreadMessagesCountDto
import hu.kocsisgeri.betterneptun.data.model.UserInfoDto
import retrofit2.http.GET
import retrofit2.http.Headers
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
}