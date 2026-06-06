package hu.kocsisgeri.betterneptun.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalWithComputedDefaultOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import hu.kocsisgeri.betterneptun.domain.service.LocalizationService
import hu.kocsisgeri.betterneptun.localization.preview.PreviewLocalizationServiceImpl
import hu.kocsisgeri.betterneptun.localization.service.LocalizationProviderScope

val LocalLocalizer =
    compositionLocalWithComputedDefaultOf<LocalizationService> {
        PreviewLocalizationServiceImpl(LocalContext.currentValue)
    }

@Composable
fun LocalizationKey.localized(): String {
    val localizer = LocalLocalizer.current
    val languages by localizer.languages.collectAsStateWithLifecycle()

    return remember(this, languages) {
        localizer.localized(this)
    }
}

@Composable
fun localized(key: String): String {
    val localizer = LocalLocalizer.current
    val languages by localizer.languages.collectAsStateWithLifecycle()

    return remember(key, languages) {
        localizer.localized(object : LocalizationKey {
            override val key: String = key
            override val args: Array<String> = emptyArray()
        })
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
