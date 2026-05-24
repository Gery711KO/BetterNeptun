package hu.kocsisgeri.betterneptun.domain.service

import androidx.annotation.StringRes
import hu.kocsisgeri.betterneptun.domain.initializable.Initializable
import hu.kocsisgeri.betterneptun.domain.model.localization.Language
import kotlinx.coroutines.flow.StateFlow

interface LocalizationService: Initializable {

    val languages: StateFlow<List<Language>>

    suspend fun changeLanguage(languageKey: String)

    fun localized(key: String, vararg args: String): String
    fun localized(@StringRes id: Int, vararg args: String): String
}
