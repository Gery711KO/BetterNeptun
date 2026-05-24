package hu.kocsisgeri.betterneptun.domain.usecase.subjects

import hu.kocsisgeri.betterneptun.domain.model.neptun.ApiResult
import hu.kocsisgeri.betterneptun.domain.model.neptun.Term
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

@Factory
class GetTermsUseCase(private val neptunRepository: NeptunRepository) {

    operator fun <T> invoke(
        mapTerm: (Term) -> T
    ) = neptunRepository.terms.map { result ->
        when (result) {
            is ApiResult.Success<List<Term>> -> ApiResult.Success(result.data.map(mapTerm))
            is ApiResult.Error -> ApiResult.Error(result.error)
            ApiResult.Loading -> ApiResult.Loading
        }
    }
}
