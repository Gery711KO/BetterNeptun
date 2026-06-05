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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalWithComputedDefaultOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
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
            navigationEntries: Collection<NavigationEntry>,
            onCurrentScreenLoaded: (Boolean) -> Unit,
        ) {
            SharedTransitionLayout {
                ProvideSharedTransitionScope {
                    NavDisplay(
                        sharedTransitionScope = this,
                        backStack = navigator.backStack,
                        onBack = { navigator.navigateBack() },
                        entryProvider = entryProvider {
                            navigationEntries.forEach { destination ->
                                destinationEntry(
                                    clazz = destination.key,
                                    content = { screen ->
                                        ScreenLoadState(
                                            navigator = navigator,
                                            currentScreen = screen,
                                            onCurrentScreenLoaded = onCurrentScreenLoaded
                                        )

                                        destination.content(screen)
                                    }
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

        @Composable
        private fun ScreenLoadState(
            navigator: Navigator,
            currentScreen: NavKey,
            onCurrentScreenLoaded: (Boolean) -> Unit
        ) {
            val lifeCycleState by LocalLifecycleOwner.current.lifecycle.currentStateAsState()
            LaunchedEffect(lifeCycleState) {
                if (currentScreen == navigator.currentScreen) {
                    onCurrentScreenLoaded(
                        lifeCycleState.isAtLeast(
                            state = Lifecycle.State.STARTED
                        )
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
    override val backStack = mutableStateListOf(startDestination)
    override val currentScreen: NavKey by derivedStateOf { backStack.last() }

    private var canNavigate: Boolean by mutableStateOf(true)

    @Composable
    override fun Content() {
        Navigator.DefaultNavDisplay(
            navigator = this,
            navigationEntries = navigationEntries,
            transitionSpec = { findTransition(isPopTransition = false) },
            popTransitionSpec = { findTransition(isPopTransition = true) },
            predictivePopTransitionSpec = { findTransition(isPopTransition = true) },
            onCurrentScreenLoaded = { canNavigate = it },
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        )
    }

    override fun navigateTo(navigationKey: NavKey) {
        if (canNavigate) backStack.add(navigationKey)
    }

    override fun navigateToInclusive(navigationKey: NavKey) {
        if (canNavigate) {
            backStack.add(navigationKey)
            backStack.removeIf { navigationKey != it }
        }
    }

    override fun navigateBack(): Boolean {
        if (backStack.size > 1 && canNavigate) {
            backStack.removeLastOrNull()
            return true
        } else {
            return false
        }
    }

    private fun AnimatedContentTransitionScope<Scene<NavKey>>.findTransition(
        isPopTransition: Boolean
    ): ContentTransform {
        val isToSplash = targetState.isSubclassOf(SplashTransition::class)
        val isFromSplash = initialState.isSubclassOf(SplashTransition::class)
        val isToShared = targetState.isSubclassOf(SharedBoundsTransition::class)
        val isFromShared = initialState.isSubclassOf(SharedBoundsTransition::class)

        return when {
            isToSplash && isFromSplash -> {
                if (isPopTransition) Transition.popSplashTransition(this)
                else Transition.splashTransition(this)
            }

            isToShared && isFromShared -> Transition.sharedTransition(this)
            else -> {
                if (isPopTransition) Transition.popSlideTransition(this)
                else Transition.slideTransition(this)
            }
        }
    }
}

private fun <T : NavKey> EntryProviderScope<NavKey>.destinationEntry(
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
private fun SharedTransitionScope.ProvideSharedTransitionScope(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalSharedTransitionScope provides this,
        content = content
    )
}
