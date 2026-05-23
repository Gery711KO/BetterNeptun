package hu.kocsisgeri.betterneptun.domain.model.neptun

data class Term(
    val id: String,
    val index : Int = 0,
    val semesterTitle: String,
    val semesterFulfilledCredits: Int?,
    val semesterTakenCredits: Int?,
    val allFulfilledCredits: Int?,
    val allTakenCredits: Int?,
)