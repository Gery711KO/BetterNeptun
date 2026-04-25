package hu.kocsisgeri.betterneptun.data.api

import hu.kocsisgeri.betterneptun.data.model.AuthenticationRequestDto
import hu.kocsisgeri.betterneptun.data.model.AuthenticationResponseDto
import hu.kocsisgeri.betterneptun.data.model.UnreadMessagesCountDto
import hu.kocsisgeri.betterneptun.data.model.UserInfoDto
import hu.kocsisgeri.betterneptun.data.api.network.NetworkResponse
import hu.kocsisgeri.betterneptun.data.model.ApiResponseDto
import hu.kocsisgeri.betterneptun.data.model.MessageListDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface APIService {

    @Headers("Content-Type: application/json")
    @POST("Account/Authenticate")
    suspend fun authenticate(
        @Body body: AuthenticationRequestDto
    ): NetworkResponse<ApiResponseDto<AuthenticationResponseDto>, String>

    @Headers("Content-Type: application/json")
    @GET("UserInfo")
    suspend fun getUserInfo(
        @Header("Authorization") token: String,
    ): NetworkResponse<ApiResponseDto<UserInfoDto>, String>

    @Headers("Content-Type: application/json")
    @GET("Message/GetUnreadedMessagesCount")
    suspend fun getUnreadMessagesCount(
        @Header("Authorization") token: String,
    ): NetworkResponse<ApiResponseDto<UnreadMessagesCountDto>, String>

    @Headers("Content-Type: application/json")
    @GET("Message/GetReceivedMessages")
    suspend fun getReceivedMessages(
        @Header("Authorization") token: String,
        @Query("firstRow") firstRow: Int,
        @Query("lastRow") lastRow: Int,
        @Query("filterType") filterType: Int = 0
    ): NetworkResponse<ApiResponseDto<MessageListDto>, String>
}