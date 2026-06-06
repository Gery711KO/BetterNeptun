package hu.kocsisgeri.betterneptun.ui.navigation.registry

import org.koin.core.annotation.Single

/**
 * A registry class that serves as a central collection point for all [NavigationEntry] instances.
 *
 * This class is managed as a singleton by Koin and automatically aggregates all registered
 * navigation entries provided in the dependency graph, allowing the navigation system
 * to dynamically discover and configure routes.
 *
 * @property navigationEntries The list of all registered navigation modules and their configurations.
 */
@Single
class NavigationRegistry(
    val navigationEntries: List<NavigationEntry>
)
