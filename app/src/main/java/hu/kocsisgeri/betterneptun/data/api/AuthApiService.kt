package hu.kocsisgeri.betterneptun.data.api

import hu.kocsisgeri.betterneptun.data.model.ApiResponseDto
import hu.kocsisgeri.betterneptun.data.model.AuthenticationRequestDto
import hu.kocsisgeri.betterneptun.data.model.AuthenticationResponseDto
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApiService {

    @Headers("Content-Type: application/json")
    @POST("Account/Authenticate")
    suspend fun authenticate(
        @Body body: AuthenticationRequestDto
    ): ApiResponseDto<AuthenticationResponseDto>
}