package hu.kocsisgeri.betterneptun.core.network.api

import hu.kocsisgeri.betterneptun.core.network.model.neptun.ApiResponseDto
import hu.kocsisgeri.betterneptun.core.network.model.neptun.AuthenticationRequestDto
import hu.kocsisgeri.betterneptun.core.network.model.neptun.AuthenticationResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.koin.core.annotation.Named
import org.koin.core.annotation.Singleton

@Singleton
internal class AuthApiServiceImpl(
    @Named("AuthClient") private val client: HttpClient
) : AuthApiService {

    override suspend fun authenticate(
        body: AuthenticationRequestDto
    ): ApiResponseDto<AuthenticationResponseDto> {
        return client.post("Account/Authenticate") {
            contentType(ContentType.Application.Json)
            setBody<AuthenticationRequestDto>(body)
        }.body()
    }
}
