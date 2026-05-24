package hu.kocsisgeri.betterneptun.domain.usecase.home

import hu.kocsisgeri.betterneptun.domain.repository.login.LoginRepository

class GetStudentDataUseCase(private val loginRepository: LoginRepository) {

    operator fun invoke() = loginRepository.studentData
}
