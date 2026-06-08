package hu.kocsisgeri.betterneptun.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
import hu.kocsisgeri.betterneptun.ui.navigation.utils.ProvideSharedTransitionScope
import hu.kocsisgeri.betterneptun.ui.navigation.utils.isSubclassOf
import org.koin.core.annotation.KoinExperimentalAPI
import kotlin.reflect.KClass

internal val LocalSharedTransitionScope =
    compositionLocalWithComputedDefaultOf<SharedTransitionScope> {
        error("No shared transition scope provided")
    }

/**
 * Interface defining the navigation operations and state management for the application.
 *
 * It manages a backstack of [NavKey]s and provides methods for forward and backward
 * navigation, as well as high-level UI rendering via the [Content] composable.
 */
interface Navigator {

    /**
     * The current stack of navigation keys representing the history of screens.
     * The last element in the list represents the currently active screen.
     */
    val backStack: List<NavKey>

    /**
     * Represents the key of the screen currently at the top of the [backStack].
     */
    val currentScreen: NavKey

    /**
     * Renders the UI content corresponding to the current state of the navigation backstack.
     *
     * This function acts as the primary entry point for displaying screens, managing
     * their lifecycle, and applying the appropriate transitions between destinations.
     */
    @Composable
    fun Content()

    /**
     * Navigates to a new destination by adding the provided [navigationKey] to the backstack.
     * The navigation only occurs if the current state allows navigation (e.g., the current screen is loaded).
     *
     * @param navigationKey The key representing the destination screen to navigate to.
     */
    fun navigateTo(navigationKey: NavKey)

    /**
     * Navigates to the specified [navigationKey] and clears the entire backstack,
     * making the new destination the only entry in the stack.
     *
     * @param navigationKey The key representing the destination screen to navigate to.
     */
    fun navigateToInclusive(navigationKey: NavKey)

    /**
     * Navigates one step back in the backstack if possible.
     *
     * @return `true` if a screen was successfully removed from the backstack,
     * `false` if the backstack contains only the start destination or if navigation is currently disabled.
     */
    fun navigateBack(): Boolean

    companion object {

        /**
         * Creates and returns an instance of the [Navigator] with the specified starting destination
         * and available navigation entries.
         *
         * @param startDestination The initial [NavKey] to be placed on the backstack.
         * @param navigationEntries A collection of [NavigationEntry] objects that define the mapping
         * between keys and their respective Composable screens.
         * @return A new [Navigator] instance initialized with the provided configuration.
         */
        fun createNavigator(
            startDestination: NavKey,
            navigationEntries: Collection<NavigationEntry>
        ): Navigator = DefaultNavigator(
            startDestination = startDestination,
            navigationEntries = navigationEntries
        )

        /**
         * A composable function that provides the default implementation for displaying navigation content.
         * It integrates with [NavDisplay] and supports shared element transitions via [SharedTransitionLayout].
         *
         * @param modifier The modifier to be applied to the navigation display container.
         * @param navigator The [Navigator] instance managing the backstack and navigation state.
         * @param entryDecorators A list of [NavEntryDecorator]s used to provide additional functionality
         * (like SaveableState or ViewModels) to the navigation entries.
         * @param transitionSpec The transition animation used when navigating forward.
         * @param popTransitionSpec The transition animation used when navigating back.
         * @param predictivePopTransitionSpec The transition animation used for predictive back gestures.
         * @param navigationEntries A collection of [NavigationEntry] objects defining the available routes and their content.
         * @param onCurrentScreenLoaded A callback triggered when the current screen's lifecycle state changes,
         * returning true if the screen is at least in the [Lifecycle.State.STARTED] state.
         */
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
