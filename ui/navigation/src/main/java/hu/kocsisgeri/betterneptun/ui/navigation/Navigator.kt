package hu.kocsisgeri.betterneptun.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import hu.kocsisgeri.betterneptun.ui.navigation.destination.HomeDestination
import hu.kocsisgeri.betterneptun.ui.navigation.destination.LoadingDestination
import hu.kocsisgeri.betterneptun.ui.navigation.destination.LoginDestination
import hu.kocsisgeri.betterneptun.ui.navigation.registry.NavigationRegistry
import hu.kocsisgeri.betterneptun.ui.navigation.utils.checkType
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
            destinations: List<NavigationRegistry<NavKey>>
        ): Navigator = DefaultNavigator(
            startDestination = startDestination,
            destinations = destinations
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
            destinations: List<NavigationRegistry<NavKey>>
        ) {
            SharedTransitionLayout {
                provideSharedTransitionScope {
                    NavDisplay(
                        sharedTransitionScope = this,
                        backStack = navigator.backStack,
                        onBack = { navigator.navigateBack() },
                        entryProvider = entryProvider {
                            destinations.forEach { registry ->
                                destinationEntry(
                                    clazz = registry.navKey,
                                    content = { registry.Content(it) }
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
    val destinations: List<NavigationRegistry<NavKey>>,
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
                val isFromLoading = initialState.checkType(LoadingDestination)
                val isFromLogin = initialState.checkType(LoginDestination)
                val isToHome = targetState.checkType(HomeDestination)
                val isToLogin = targetState.checkType(LoginDestination)

                if ((isFromLogin && isToHome) || isToLogin || isFromLoading) {
                    fadeIn(animationSpec = tween(500)) + slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(500, 300)
                    ) + scaleIn(
                        initialScale = 0.6f,
                        animationSpec = tween(500, 300)
                    ) togetherWith slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(500, 300),
                    ) + scaleOut(
                        targetScale = 0.6f,
                        animationSpec = tween(500)
                    ) + fadeOut(animationSpec = tween(500, 500))
                } else {
                    slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                }
            },
            popTransitionSpec = {
                slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
            },
            predictivePopTransitionSpec = {
                slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
            },
            destinations = destinations,
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
        clazzContentKey = { it.toString() },
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
