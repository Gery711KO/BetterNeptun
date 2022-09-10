package hu.kocsisgeri.betterneptun.domain.api.dto

data class AddedSubjectsResponseDto(
    val AddedSubjectsList : List<AddedSubjectDto>
)

data class AddedSubjectDto(
    val SubjectID: Int,
    val SubjectCode: String,
    val SubjectCredit : String,
    val SubjectName: String,
    val SubjectRequirement: String,
    val SubjectType: String,
    val TermId: Int,
)