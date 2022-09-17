package hu.kocsisgeri.betterneptun.domain.api

import hu.kocsisgeri.betterneptun.domain.api.dto.*
import hu.kocsisgeri.betterneptun.domain.api.network.NetworkResponse
import hu.kocsisgeri.betterneptun.ui.model.AddedSubjectRequest
import hu.kocsisgeri.betterneptun.ui.model.MarkBookRequest
import hu.kocsisgeri.betterneptun.ui.model.MessageReader
import hu.kocsisgeri.betterneptun.ui.model.NeptunUser
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface APIService {

    @Headers("Content-Type: application/json")
    @POST("MobileService.svc/GetMessages")
    suspend fun fetchMessages(
        @Body body: NeptunUser
    ): NetworkResponse<MessageResponseDto, String>

    @Headers("Content-Type: application/json")
    @POST("MobileService.svc/SetReadedMessage")
    suspend fun markMessageAsRead(
        @Body body: MessageReader
    ): NetworkResponse<NeptunUser, String>

    @Headers("Content-Type: application/json")
    @GET("Login.aspx")
    suspend fun getLogin(): NetworkResponse<String, String>

    @Headers("Content-Type: application/json")
    @POST("login.aspx/CheckLoginEnable")
    suspend fun checkLogin(
        @Body body: LoginRequestDto
    ): NetworkResponse<LoginResponseDto, String>

    @Headers("Content-Type: application/json")
    @POST("login.aspx/GetMaxTryNumber")
    suspend fun checkTry(
        @Body body: LoginRequestDto
    ): NetworkResponse<LoginResponseDto, String>

    @Headers("Content-Type: application/json")
    @POST("login.aspx/SavePopupState")
    suspend fun checkPopUp(
        @Body popupDto: PopupDto
    ): NetworkResponse<String, String>

    @Headers("Content-Type: application/json")
    @POST("MobileService.svc/GetTrainings")
    suspend fun fetchUserData(
        @Body body: NeptunUser
    ): NetworkResponse<TrainingResponseDto, String>

    @Headers("Content-Type: application/json")
    @POST("MobileService.svc/GetAddedSubjects")
    suspend fun fetchAddedSubjects(
        @Body body: AddedSubjectRequest
    ): NetworkResponse<AddedSubjectsResponseDto, String>

    @Headers("Content-Type: application/json")
    @POST("MobileService.svc/GetMarkbookData")
    suspend fun fetchMarkBookData(
        @Body body: MarkBookRequest
    ): NetworkResponse<MarkBookDataResponseDto, String>
}