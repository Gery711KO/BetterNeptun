package hu.kocsisgeri.betterneptun.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SubjectDto(
    val subjectId: String,
    val subjectName: String,
    val subjectCode: String,
    val subjectCredit: Int,
    val requirementType: String,
    val termId: String,
    val uiDisplayState: UiDisplayState
) {

    @Serializable
    data class UiDisplayState(
        val type: Int,
        val reasons: List<String>
    )
}