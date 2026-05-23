package hu.kocsisgeri.betterneptun.data.mapper

import hu.kocsisgeri.betterneptun.core.network.model.neptun.TermAveragesDto
import hu.kocsisgeri.betterneptun.core.network.model.neptun.TermDto
import hu.kocsisgeri.betterneptun.domain.model.neptun.Average
import hu.kocsisgeri.betterneptun.domain.model.neptun.Term

fun List<TermDto>.toTermDomain(): List<Term> {
    return reversed().mapIndexed { index, term ->
        Term(
            id = term.id,
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
