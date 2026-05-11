package hu.kocsisgeri.betterneptun.core.network.api

import hu.kocsisgeri.betterneptun.core.network.model.ApiResponseDto
import hu.kocsisgeri.betterneptun.core.network.model.AuthenticationRequestDto
import hu.kocsisgeri.betterneptun.core.network.model.AuthenticationResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("Account/Authenticate")
    suspend fun authenticate(
        @Body body: AuthenticationRequestDto
    ): ApiResponseDto<AuthenticationResponseDto>
}