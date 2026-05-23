package hu.kocsisgeri.betterneptun.ui.core.helper

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalWithComputedDefaultOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import hu.kocsisgeri.betterneptun.domain.model.localization.Language
import hu.kocsisgeri.betterneptun.domain.service.LocalizationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.ui.res.stringResource

val LocalLocalizer =
    compositionLocalWithComputedDefaultOf<LocalizationService> {
        defaultLocalizationService(LocalContext.currentValue)
    }

@Composable
fun localized(
    @StringRes id: Int,
    vararg args: String = emptyArray()
): String {
    return localized(stringResource(id), *args)
}

@Composable
fun localized(
    key: String,
    vararg args: String = emptyArray()
): String {
    val localizer = LocalLocalizer.current
    val languages by localizer.languages.collectAsStateWithLifecycle()

    return remember(languages) {
        localizer.localized(key, *args)
    }
}

private fun defaultLocalizationService(context: Context) = object: LocalizationService {
    override val languages: StateFlow<List<Language>> =
        MutableStateFlow(listOf(Language.DEFAULT))

    override suspend fun changeLanguage(language: Language) {}

    override fun localized(key: String, vararg args: String): String {
        return key
    }

    override fun localized(id: Int, vararg args: String): String {
        return context.getString(id)
    }

    override val isInitialized: StateFlow<Boolean> = MutableStateFlow(true)

    override suspend fun initialize() {}
}