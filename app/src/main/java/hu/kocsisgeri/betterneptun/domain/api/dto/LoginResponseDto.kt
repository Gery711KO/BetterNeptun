package hu.kocsisgeri.betterneptun.domain.api.dto

data class LoginResponseDto(
    val d: String?
) {
    fun isSuccess() : Boolean {
        return d?.contains("Sikeres", ignoreCase = true) == true
    }

    fun getError() : String {
        return d?.split(",")?.get(1)?.split(":")?.get(1)
            ?.removePrefix("\'")?.removeSuffix("\'").toString()
    }
}