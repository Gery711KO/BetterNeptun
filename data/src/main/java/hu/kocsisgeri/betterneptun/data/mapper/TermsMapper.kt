package hu.kocsisgeri.betterneptun.data.mapper

import hu.kocsisgeri.betterneptun.core.network.model.ExtendedTermDto
import hu.kocsisgeri.betterneptun.core.network.model.TermAveragesDto
import hu.kocsisgeri.betterneptun.core.network.model.TermDto
import hu.kocsisgeri.betterneptun.domain.model.Average
import hu.kocsisgeri.betterneptun.domain.model.ExtendedTerm
import hu.kocsisgeri.betterneptun.domain.model.Term

fun List<TermDto>.toTermDomain(): List<Term> {
    return reversed().mapIndexed { index, term ->
        Term(
            index = index,
            semesterTitle = term.text,
            semesterFulfilledCredits = term.completedCredit,
            semesterTakenCredits = term.creditSum,
            allFulfilledCredits = take(index + 1)
                .sumOf { it.completedCredit },
            allTakenCredits = take(index + 1).sumOf { it.creditSum },
        )
    }
}

fun List<TermAveragesDto.TermAverage>.toAverageDomain() = mapIndexed { index, average ->
    Average(
        index = index,
        normalAverage = average.average,
        commutativeAverage = average.sumAverage
    )
}


fun List<ExtendedTermDto>.toExtendedTermDomain() = map {
    ExtendedTerm(
        termDataStatus = it.termDataStatus,
        term = it.term,
        termId = it.termId,
        financialStatus = it.financialStatus,
        semester = it.semester,
        registration = it.registration,
        closing = it.closing,
        usedSupportedSemestersInTotal = it.usedSupportedSemestersInTotal,
        studentTrainingTermDataId = it.studentTrainingTermDataId
    )
}