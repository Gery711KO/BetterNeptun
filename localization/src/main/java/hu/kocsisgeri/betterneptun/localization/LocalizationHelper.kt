package hu.kocsisgeri.betterneptun.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalWithComputedDefaultOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import hu.kocsisgeri.betterneptun.domain.service.LocalizationService
import hu.kocsisgeri.betterneptun.localization.service.LocalizationProviderScope
import hu.kocsisgeri.betterneptun.localization.service.defaultLocalizationService

internal val LocalLocalizer =
    compositionLocalWithComputedDefaultOf {
        defaultLocalizationService()
    }

@Composable
fun localized(
    key: LocalizationKey,
    vararg args: String = emptyArray()
): String {
    val localizer = LocalLocalizer.current
    val languages by localizer.languages.collectAsStateWithLifecycle()

    return remember(languages, args) {
        localizer.localized(key, *args)
    }
}

@Composable
fun rememberLocalizationProviderScope(
    localization: LocalizationService,
): LocalizationProviderScope = remember(localization) {
    LocalizationProviderScope(localization)
}

@Composable
fun LocalizationProviderScope.ProvideLocalization(
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        value = LocalLocalizer provides localizer,
        content = content
    )
}
