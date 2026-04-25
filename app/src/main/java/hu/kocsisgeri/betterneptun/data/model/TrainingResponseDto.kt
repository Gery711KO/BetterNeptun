package hu.kocsisgeri.betterneptun.data.model

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