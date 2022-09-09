package hu.kocsisgeri.betterneptun.domain.api.dto

data class TrainingResponseDto(
    val ErrorMessage: String?,
    val NeptunCode: String?,
    val TrainingList: List<TrainingDto>?
)

data class TrainingDto(
    val Code: String,
    val Description: String,
    val Id: Int
)