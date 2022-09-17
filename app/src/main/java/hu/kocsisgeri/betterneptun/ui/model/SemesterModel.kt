package hu.kocsisgeri.betterneptun.ui.model

data class SemesterModel(
    val index : Int = 0,
    val semesterTitle: String,
    val status: String,
    val moneyStatus: String,
    val semesterFulfilledCredits: Int?,
    val semesterTakenCredits: Int?,
    val allFulfilledCredits: Int?,
    val allTakenCredits: Int?,
    val normalAverage: Double?,
    val commutativeAverage: Double?
)