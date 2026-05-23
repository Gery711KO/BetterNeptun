package hu.kocsisgeri.betterneptun.ui.core.helper

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalWithComputedDefaultOf
import hu.kocsisgeri.betterneptun.domain.model.localization.Language
import hu.kocsisgeri.betterneptun.domain.service.LocalizationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

val LocalLocalizer =
    compositionLocalWithComputedDefaultOf<LocalizationService> {
        object: LocalizationService {
            override val languages: StateFlow<List<Language>> =
                MutableStateFlow(listOf(Language.DEFAULT))

            override suspend fun changeLanguage(language: Language) {}

            override fun localized(id: Int, vararg args: String): String {
                return id.toString()
            }

            override fun localized(key: String, vararg args: String): String {
                return key
            }

            override val isInitialized: StateFlow<Boolean> = MutableStateFlow(true)

            override suspend fun initialize() {}
        }
    }

@Composable
fun localized(
    @StringRes id: Int,
    vararg args: String = emptyArray()
): String {
    return LocalLocalizer.current.localized(id, *args)
}

@Composable
fun localized(
    key: String,
    vararg args: String = emptyArray()
): String {
    return LocalLocalizer.current.localized(key, *args)
}