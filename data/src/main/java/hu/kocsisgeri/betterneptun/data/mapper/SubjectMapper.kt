package hu.kocsisgeri.betterneptun.data.mapper

import hu.kocsisgeri.betterneptun.data.model.SubjectDto
import hu.kocsisgeri.betterneptun.domain.model.Subject

fun List<SubjectDto>.toSubjectDomain() = map {
    Subject(
        subjectId = it.subjectId,
        subjectCode = it.subjectCode,
        subjectCredit = it.subjectCredit,
        subjectName = it.subjectName,
        subjectRequirement = it.requirementType,
        termId = it.termId,
        isCompleted = it.uiDisplayState.reasons.contains("Teljesített")
    )
}