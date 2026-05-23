package hu.kocsisgeri.betterneptun.domain.repository.login

import hu.kocsisgeri.betterneptun.domain.model.neptun.ApiResult
import hu.kocsisgeri.betterneptun.domain.model.neptun.StudentData
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface LoginRepository {

    val studentData: StateFlow<ApiResult<StudentData>>
    val shouldAutoLogin: SharedFlow<Boolean>

    suspend fun login(neptunCode: String, password: String)
    suspend fun silentLogin()

    fun saveCurrentUser(neptunCode: String, password: String)
    fun saveAutoLoginPreference(shouldAutoLogin: Boolean)

    fun purge()
}