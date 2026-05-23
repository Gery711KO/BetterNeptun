package hu.kocsisgeri.betterneptun.data.mapper

import hu.kocsisgeri.betterneptun.core.network.model.neptun.SubjectDto
import hu.kocsisgeri.betterneptun.domain.model.neptun.Subject

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