package hu.kocsisgeri.betterneptun.domain.usecase.home

import hu.kocsisgeri.betterneptun.domain.repository.login.LoginRepository
import org.koin.core.annotation.Factory

@Factory
class GetStudentDataUseCase(private val loginRepository: LoginRepository) {

    operator fun invoke() = loginRepository.studentData
}
