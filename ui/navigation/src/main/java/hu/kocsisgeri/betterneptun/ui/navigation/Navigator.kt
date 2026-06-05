package hu.kocsisgeri.betterneptun.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalWithComputedDefaultOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.defaultPopTransitionSpec
import androidx.navigation3.ui.defaultPredictivePopTransitionSpec
import androidx.navigation3.ui.defaultTransitionSpec
import androidx.navigationevent.NavigationEvent
import hu.kocsisgeri.betterneptun.ui.navigation.registry.NavigationEntry
import hu.kocsisgeri.betterneptun.ui.navigation.transition.SharedBoundsTransition
import hu.kocsisgeri.betterneptun.ui.navigation.transition.SplashTransition
import hu.kocsisgeri.betterneptun.ui.navigation.transition.Transition
import hu.kocsisgeri.betterneptun.ui.navigation.utils.isSubclassOf
import org.koin.core.annotation.KoinExperimentalAPI
import kotlin.reflect.KClass

internal val LocalSharedTransitionScope =
    compositionLocalWithComputedDefaultOf<SharedTransitionScope> {
        error("No shared transition scope provided")
    }

interface Navigator {
    val backStack: List<NavKey>
    val currentScreen: NavKey

    @Composable
    fun getSharedTransitionScope(): SharedTransitionScope

    @Composable
    fun Content()

    fun navigateTo(navigationKey: NavKey)
    fun navigateToInclusive(navigationKey: NavKey)
    fun navigateBack(): Boolean

    companion object {
        fun createNavigator(
            startDestination: NavKey,
            navigationEntries: Collection<NavigationEntry>
        ): Navigator = DefaultNavigator(
            startDestination = startDestination,
            navigationEntries = navigationEntries
        )

        @OptIn(KoinExperimentalAPI::class)
        @Composable
        fun DefaultNavDisplay(
            modifier: Modifier,
            navigator: Navigator,
            entryDecorators: List<NavEntryDecorator<NavKey>> = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            transitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform =
                defaultTransitionSpec(),
            popTransitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform =
                defaultPopTransitionSpec(),
            predictivePopTransitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.(
                @NavigationEvent.SwipeEdge Int
            ) -> ContentTransform = defaultPredictivePopTransitionSpec(),
            navigationEntries: Collection<NavigationEntry>
        ) {
            SharedTransitionLayout {
                provideSharedTransitionScope {
                    NavDisplay(
                        sharedTransitionScope = this,
                        backStack = navigator.backStack,
                        onBack = { navigator.navigateBack() },
                        entryProvider = entryProvider {
                            navigationEntries.forEach { destination ->
                                destinationEntry(
                                    clazz = destination.key,
                                    content = { destination.content(it) }
                                )
                            }
                        },
                        entryDecorators = entryDecorators,
                        transitionSpec = transitionSpec,
                        popTransitionSpec = popTransitionSpec,
                        predictivePopTransitionSpec = predictivePopTransitionSpec,
                        modifier = modifier
                    )
                }
            }
        }
    }
}

@Immutable
private data class DefaultNavigator(
    val startDestination: NavKey,
    val navigationEntries: Collection<NavigationEntry>,
) : Navigator {
    override val backStack = mutableStateListOf<NavKey>(startDestination)
    override val currentScreen: NavKey by derivedStateOf { backStack.last() }

    @Composable
    override fun getSharedTransitionScope() = LocalSharedTransitionScope.current

    @Composable
    override fun Content() {
        Navigator.DefaultNavDisplay(
            navigator = this,
            transitionSpec = {
                val isToSplash = targetState.isSubclassOf(SplashTransition::class)
                val isFromSplash = targetState.isSubclassOf(SplashTransition::class)
                val isToShared = targetState.isSubclassOf(SharedBoundsTransition::class)
                val isFromShared = targetState.isSubclassOf(SharedBoundsTransition::class)

                when {
                    isToSplash && isFromSplash -> Transition.splashTransition(this)
                    isToShared && isFromShared -> Transition.sharedTransition(this)
                    else -> Transition.slideTransition(this)
                }
            },
            popTransitionSpec = {
                val isFromSplash = initialState.isSubclassOf(SplashTransition::class)
                val isFromShared = initialState.isSubclassOf(SharedBoundsTransition::class)

                when {
                    isFromSplash-> Transition.popSplashTransition(this)
                    isFromShared -> Transition.sharedTransition(this)
                    else -> Transition.popSlideTransition(this)
                }
            },
            predictivePopTransitionSpec = {
                val isFromSplash = initialState.isSubclassOf(SplashTransition::class)
                val isFromShared = initialState.isSubclassOf(SharedBoundsTransition::class)

                when {
                    isFromSplash -> Transition.popSplashTransition(this)
                    isFromShared -> Transition.sharedTransition(this)
                    else -> Transition.popSlideTransition(this)
                }
            },
            navigationEntries = navigationEntries,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        )
    }

    override fun navigateTo(navigationKey: NavKey) {
        backStack.add(navigationKey)
    }

    override fun navigateToInclusive(navigationKey: NavKey) {
        backStack.add(navigationKey)
        backStack.removeIf { navigationKey != it }
    }

    override fun navigateBack(): Boolean {
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
            return true
        } else {
            return false
        }
    }
}

internal fun <T : NavKey> EntryProviderScope<NavKey>.destinationEntry(
    clazz: KClass<out T>,
    content: @Composable (T) -> Unit,
) {
    addEntryProvider(
        clazz = clazz,
        clazzContentKey = { clazz.java },
        content = content
    )
}

@Composable
private fun SharedTransitionScope.provideSharedTransitionScope(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalSharedTransitionScope provides this,
        content = content
    )
}
