package hu.kocsisgeri.betterneptun.domain.model.neptun

data class Subject(
    val subjectId: String,
    val subjectCode: String,
    val subjectCredit: Int,
    val subjectName: String,
    val subjectRequirement: String,
    val termId: String,
    val isCompleted: Boolean
)