package hu.kocsisgeri.betterneptun.domain.api.datasource

import hu.kocsisgeri.betterneptun.domain.api.dto.*
import hu.kocsisgeri.betterneptun.domain.api.network.NetworkResponse
import hu.kocsisgeri.betterneptun.domain.model.StudentData
import hu.kocsisgeri.betterneptun.ui.model.NeptunUser

interface NetworkDataSource {
    suspend fun initiateLogin(userData: LoginRequestDto) : NetworkResponse<LoginResponseDto, String>
    suspend fun getLogin(): NetworkResponse<String, String>
    suspend fun checkLogin(userData: LoginRequestDto): NetworkResponse<LoginResponseDto, String>
    suspend fun checkTry(userData: LoginRequestDto): NetworkResponse<LoginResponseDto, String>
    suspend fun checkPopUp(popupDto: PopupDto): NetworkResponse<String, String>
    suspend fun getMessages(neptunUser: NeptunUser): NetworkResponse<MessageResponseDto, String>
    suspend fun getTrainings(neptunUser: NeptunUser): NetworkResponse<TrainingResponseDto, String>
    suspend fun getData(): StudentData
    suspend fun getCourses() : CourseResponseDto
}