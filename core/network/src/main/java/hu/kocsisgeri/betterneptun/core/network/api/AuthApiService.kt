package hu.kocsisgeri.betterneptun.core.network.api

import hu.kocsisgeri.betterneptun.core.network.model.neptun.ApiResponseDto
import hu.kocsisgeri.betterneptun.core.network.model.neptun.AuthenticationRequestDto
import hu.kocsisgeri.betterneptun.core.network.model.neptun.AuthenticationResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("Account/Authenticate")
    suspend fun authenticate(
        @Body body: AuthenticationRequestDto
    ): ApiResponseDto<AuthenticationResponseDto>
}
