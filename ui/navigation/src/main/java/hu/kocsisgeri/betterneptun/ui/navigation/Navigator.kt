package hu.kocsisgeri.betterneptun.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
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
import hu.kocsisgeri.betterneptun.domain.error.ErrorReceiver
import hu.kocsisgeri.betterneptun.domain.error.model.ErrorContent
import hu.kocsisgeri.betterneptun.ui.designsystem.composable.snackbar.StackedSnackbarAnimation
import hu.kocsisgeri.betterneptun.ui.designsystem.composable.snackbar.StackedSnackbarDuration
import hu.kocsisgeri.betterneptun.ui.designsystem.composable.snackbar.StackedSnackbarHost
import hu.kocsisgeri.betterneptun.ui.designsystem.composable.snackbar.StackedSnackbarHostState
import hu.kocsisgeri.betterneptun.ui.designsystem.composable.snackbar.rememberStackedSnackbarHostState
import hu.kocsisgeri.betterneptun.ui.navigation.destination.GeneralErrorDestination
import hu.kocsisgeri.betterneptun.ui.navigation.registry.NavigationEntry
import hu.kocsisgeri.betterneptun.ui.navigation.transition.ErrorTransition
import hu.kocsisgeri.betterneptun.ui.navigation.transition.SharedBoundsTransition
import hu.kocsisgeri.betterneptun.ui.navigation.transition.SplashTransition
import hu.kocsisgeri.betterneptun.ui.navigation.transition.Transition
import hu.kocsisgeri.betterneptun.ui.navigation.utils.isSubclassOf
import hu.kocsisgeri.betterneptun.ui.theme.ProvideSharedTransitionScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.core.annotation.KoinExperimentalAPI
import timber.log.Timber
import kotlin.reflect.KClass

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
     * Replaces the current screen at the top of the backstack with the provided [NavKey].
     *
     * This operation removes the last entry and adds the new one, ensuring the backstack
     * size remains the same.
     *
     * @param with The key representing the new destination screen.
     */
    fun replaceCurrent(with: NavKey)

    /**
     * Navigates to the specified [navigationKey] and clears the entire backstack,
     * making the new destination the only entry in the stack.
     *
     * @param navigationKey The key representing the destination screen to navigate to.
     */
    fun navigateToInclusive(navigationKey: NavKey)

    /**
     * Navigates one step back in the backstack or to a specific destination.
     *
     * @param to The optional [NavKey] to navigate back to. If provided, the backstack is
     * popped until this destination is reached. If null, only the top entry is removed.
     * @return `true` if the backstack was successfully modified, `false` if the
     * backstack contains only the start destination or if navigation is currently disabled.
     */
    fun navigateBack(to: NavKey? = null): Boolean

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
         * @param onCurrentScreenState A callback triggered when the current screen's lifecycle state changes.
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
            onCurrentScreenState: (Lifecycle.State) -> Unit,
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
                                            onCurrentScreenState = onCurrentScreenState
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
            onCurrentScreenState: (Lifecycle.State) -> Unit
        ) {
            val lifeCycleState by LocalLifecycleOwner.current.lifecycle.currentStateAsState()
            LaunchedEffect(lifeCycleState) {
                if (currentScreen == navigator.currentScreen) {
                    onCurrentScreenState(lifeCycleState)
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
    private var canNavigateToError: Boolean by mutableStateOf(true)

    @Composable
    override fun Content() {
        val errorHandler = koinInject<ErrorReceiver>()

        val snackbarHostState = rememberStackedSnackbarHostState(
            maxStack = 5,
            animation = StackedSnackbarAnimation.Bounce
        )

        LaunchedEffect(errorHandler) {
            errorHandler
                .errorCallback
                .errorControllerGate(
                    scope = this,
                    snackbarHostState = snackbarHostState
                )
        }

        Scaffold(
            snackbarHost = { StackedSnackbarHost(hostState = snackbarHostState) },
            floatingActionButtonPosition = FabPosition.End,
        ) { padding ->
            padding // Do not process padding, underlying screens will handle it.
            Navigator.DefaultNavDisplay(
                navigator = this,
                navigationEntries = navigationEntries,
                transitionSpec = { findTransition(isPopTransition = false) },
                popTransitionSpec = { findTransition(isPopTransition = true) },
                predictivePopTransitionSpec = { findTransition(isPopTransition = true) },
                onCurrentScreenState = {
                    canNavigate = it.isAtLeast(Lifecycle.State.STARTED)
                    canNavigateToError = it.isAtLeast(Lifecycle.State.RESUMED)
                },
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            )
        }
    }

    override fun navigateTo(navigationKey: NavKey) {
        if (canNavigate) backStack.add(navigationKey)
    }

    override fun replaceCurrent(with: NavKey) {
        backStack.removeLastOrNull()
        backStack.add(with)
    }

    override fun navigateToInclusive(navigationKey: NavKey) {
        if (canNavigate) {
            backStack.add(navigationKey)
            backStack.removeIf { navigationKey != it }
        }
    }

    override fun navigateBack(to: NavKey?): Boolean {
        if (backStack.size > 1 && canNavigate) {
            to?.let {
                while (backStack.last() != to) {
                    backStack.removeLastOrNull()
                }
            } ?: backStack.removeLastOrNull()
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
        val isError = initialState.isSubclassOf(
            ErrorTransition::class
        ) || targetState.isSubclassOf(ErrorTransition::class)

        return when {
            isError -> Transition.crossFadeTransition(this)
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

    private suspend fun <T> Flow<T>.errorControllerGate(
        scope: CoroutineScope,
        snackbarHostState: StackedSnackbarHostState
    ) = onEach { error ->
        Timber.tag("ERROR-GATE").d("[$currentScreen] - Error received: $error")
    }.buffer(Channel.UNLIMITED).flatMapConcat { error ->
        val eventSource = currentScreen

        snapshotFlow { canNavigateToError }
            .filter { it }
            .take(1)
            .map { error to eventSource }
            .onEach { (error, source) ->
                Timber.tag("ERROR-GATE").d("[$source] - Currently processing: $error")
            }
    }.collect { (errorContent, source) ->
        val canNavigateToError = source::class == currentScreen::class

        if (!canNavigateToError && errorContent is ErrorContent.FullScreen) {
            Timber.tag("ERROR-GATE").d("[$source] - Navigation is not possible to: $errorContent")
        }

        when (errorContent) {
            is ErrorContent.FullScreen if canNavigateToError -> {
                if (currentScreen != GeneralErrorDestination) {
                    if (errorContent.inclusive) navigateToInclusive(
                        GeneralErrorDestination
                    ) else navigateTo(GeneralErrorDestination)
                }
            }

            is ErrorContent.Snackbar -> scope.launch {
                snackbarHostState.showWarningSnackbar(
                    title = errorContent.text,
                    duration = StackedSnackbarDuration.Short
                )
            }

            is ErrorContent.PopUp if canNavigateToError -> {
                // TODO
            }

            else -> {
                // NO-OP
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
