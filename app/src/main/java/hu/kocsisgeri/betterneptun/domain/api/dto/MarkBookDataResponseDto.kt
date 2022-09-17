package hu.kocsisgeri.betterneptun.domain.api.dto

class MarkBookDataResponseDto(
    val MarkBookList: List<MarkBookDataDto>
)

data class MarkBookDataDto(
    val ID: Int,
    val Completed: Boolean,
    val Signer: String,
    val SubjectName: String,
    val Values: String,
    val Credit: Int,
)