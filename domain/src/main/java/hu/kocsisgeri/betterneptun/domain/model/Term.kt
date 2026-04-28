package hu.kocsisgeri.betterneptun.domain.model

data class Term(
    val index : Int = 0,
    val semesterTitle: String,
    val semesterFulfilledCredits: Int?,
    val semesterTakenCredits: Int?,
    val allFulfilledCredits: Int?,
    val allTakenCredits: Int?,
)