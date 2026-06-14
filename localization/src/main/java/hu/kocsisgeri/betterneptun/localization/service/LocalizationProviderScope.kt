package hu.kocsisgeri.betterneptun.localization.service

import hu.kocsisgeri.betterneptun.domain.service.LocalizationService

/**
 * Scope class that provides access to the [LocalizationService].
 *
 * This class is typically used as a receiver in DSL-style localization builders
 * or scoped operations to provide convenient access to string localization.
 *
 * @property localizer The [LocalizationService] instance used for resolving localized strings.
 */
class LocalizationProviderScope(val localizer: LocalizationService)
