package hu.kocsisgeri.betterneptun.domain.repository.login

import hu.kocsisgeri.betterneptun.domain.model.neptun.ApiResult
import hu.kocsisgeri.betterneptun.domain.model.neptun.StudentData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface LoginRepository {

    val studentData: StateFlow<StudentData?>
    val shouldAutoLogin: SharedFlow<Boolean>

    fun login(neptunCode: String, password: String): Flow<ApiResult<StudentData>>
    fun silentLogin(): Flow<ApiResult<StudentData>>

    fun saveCurrentUser(neptunCode: String, password: String)
    fun saveAutoLoginPreference(shouldAutoLogin: Boolean)

    fun purge()
}
