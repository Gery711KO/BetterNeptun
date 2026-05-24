package hu.kocsisgeri.betterneptun.domain.repository.login

import hu.kocsisgeri.betterneptun.domain.model.neptun.ApiResult
import hu.kocsisgeri.betterneptun.domain.model.neptun.StudentData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository responsible for managing Neptun authentication, session state, and login preferences.
 *
 * This repository handles manual and silent login processes, persists user credentials,
 * and provides reactive streams to observe the current student's data and auto-login settings.
 */
interface LoginRepository {

    /**
     * A [StateFlow] representing the currently logged-in student's information.
     * Emits `null` if no user is authenticated or if the data has not been loaded yet.
     */
    val studentData: StateFlow<StudentData?>

    /**
     * A [SharedFlow] that emits whether the application should attempt to perform an automatic login.
     * This typically reflects changes in the user's saved preferences.
     */
    val shouldAutoLogin: SharedFlow<Boolean>

    /**
     * Performs a login attempt with the provided Neptun credentials.
     *
     * @param neptunCode The unique Neptun identifier of the student.
     * @param password The password associated with the Neptun account.
     * @return A [Flow] emitting the [ApiResult] of the login process, containing [StudentData] on success.
     */
    fun login(neptunCode: String, password: String): Flow<ApiResult<StudentData>>

    /**
     * Attempts to perform a login operation automatically using previously saved credentials.
     * This is typically used to restore a session upon application startup without
     * requiring manual user intervention.
     *
     * @return A [Flow] emitting the [ApiResult] of the authentication attempt.
     */
    fun silentLogin(): Flow<ApiResult<StudentData>>

    /**
     * Persists the provided user credentials to the local storage.
     *
     * @param neptunCode The Neptun identifier of the user.
     * @param password The password associated with the Neptun account.
     */
    fun saveCurrentUser(neptunCode: String, password: String)

    /**
     * Persists the user's preference regarding whether the application should automatically
     * perform a login attempt using saved credentials upon startup or session expiration.
     *
     * @param shouldAutoLogin True if automatic login should be enabled, false otherwise.
     */
    fun saveAutoLoginPreference(shouldAutoLogin: Boolean)
}
