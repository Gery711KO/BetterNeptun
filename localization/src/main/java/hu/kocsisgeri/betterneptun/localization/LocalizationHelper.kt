package hu.kocsisgeri.betterneptun.localization

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalWithComputedDefaultOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import hu.kocsisgeri.betterneptun.domain.service.LocalizationService

internal val LocalLocalizer =
    compositionLocalWithComputedDefaultOf {
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
