package hu.kocsisgeri.betterneptun.domain.repository.neptun

import hu.kocsisgeri.betterneptun.domain.model.neptun.ApiResult
import hu.kocsisgeri.betterneptun.domain.model.neptun.Average
import hu.kocsisgeri.betterneptun.domain.model.neptun.Subject
import hu.kocsisgeri.betterneptun.domain.model.neptun.Term
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository responsible for managing academic data such as subjects, terms, and grade averages.
 */
interface AcademicRepository {
    /**
     * A [StateFlow] emitting the list of subjects for a selected term, wrapped in an [ApiResult].
     */
    val subjects: StateFlow<ApiResult<List<Subject>>>

    /**
     * A [StateFlow] emitting the list of available academic terms, wrapped in an [ApiResult].
     */
    val terms: StateFlow<ApiResult<List<Term>>>

    /**
     * A [StateFlow] emitting the list of grade averages across different terms, wrapped in an [ApiResult].
     */
    val averages: StateFlow<ApiResult<List<Average>>>

    /**
     * Fetches the list of subjects for the specified term.
     *
     * @param termId The unique identifier of the academic term.
     */
    suspend fun fetchSubjects(termId: String)

    /**
     * Fetches the list of available academic terms.
     */
    suspend fun fetchTerms()

    /**
     * Fetches the academic averages for all available terms.
     */
    suspend fun fetchTermAverages()
}
