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

/**
 * A [androidx.compose.runtime.ProvidableCompositionLocal] that provides the current [LocalizationService] instance.
 *
 * This is used to access localization capabilities throughout the composition tree.
 * Defaults to preview provider [LocalizationService] when no explicit value is provided.
 */
val LocalLocalizer =
    compositionLocalWithComputedDefaultOf<LocalizationService> {
        PreviewLocalizationServiceImpl(LocalContext.currentValue)
    }

/**
 * Returns the localized string for a given [LocalizationKey].
 *
 * This function observes the current language state from the [LocalLocalizer] and
 * automatically recomposes to provide the updated translation when the language changes.
 *
 * @receiver [LocalizationKey] The unique localization key entry.
 * @return The translated string corresponding to the provided [LocalizationKey].
 */
@Composable
fun LocalizationKey.localized(): String {
    val localizer = LocalLocalizer.current
    val languages by localizer.languages.collectAsStateWithLifecycle()

    return remember(this, languages) {
        localizer.localized(this)
    }
}

/**
 * Returns the localized string for a given raw `key`.
 *
 * This function observes the current language state from the `key` and
 * automatically recomposes to provide the updated translation when the language changes.
 *
 * @param key The raw localization `key`, that is only used in rare occasions.
 * @return The translated string corresponding to the provided `key`.
 */
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


/**
 * Creates and remembers a [LocalizationProviderScope] for a given [LocalizationService].
 *
 * This function ensures that the localization scope is preserved across recompositions
 * as long as the provided [LocalizationService] instance remains the same.
 *
 * @param localization The [LocalizationService] to be wrapped within the scope.
 * @return A remembered [LocalizationProviderScope] instance.
 */
@Composable
fun rememberLocalizationProviderScope(
    localization: LocalizationService,
): LocalizationProviderScope = remember(localization) {
    LocalizationProviderScope(localization)
}

/**
 * Provides the [LocalizationService] to the composition tree via [LocalLocalizer].
 *
 * This function uses [CompositionLocalProvider] to bind the [LocalizationService]
 * instance (from the [LocalizationProviderScope]) to the [LocalLocalizer] composition local,
 * enabling nested composables to access localized strings.
 */
@Composable
fun LocalizationProviderScope.ProvideLocalization(
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        value = LocalLocalizer provides localizer,
        content = content
    )
}
