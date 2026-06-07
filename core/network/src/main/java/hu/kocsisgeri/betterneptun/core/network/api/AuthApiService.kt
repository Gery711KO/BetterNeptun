package hu.kocsisgeri.betterneptun.core.network.api

import hu.kocsisgeri.betterneptun.core.network.model.neptun.ApiResponseDto
import hu.kocsisgeri.betterneptun.core.network.model.neptun.AuthenticationRequestDto
import hu.kocsisgeri.betterneptun.core.network.model.neptun.AuthenticationResponseDto

interface AuthApiService {

    suspend fun authenticate(
        body: AuthenticationRequestDto
    ): ApiResponseDto<AuthenticationResponseDto>
}
