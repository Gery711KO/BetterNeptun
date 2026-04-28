package hu.kocsisgeri.betterneptun.ui.navigation

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalWithComputedDefaultOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.NavDisplay
import hu.kocsisgeri.betterneptun.ui.navigation.destination.HomeDestination
import hu.kocsisgeri.betterneptun.ui.navigation.destination.LoginDestination
import kotlinx.serialization.Serializable
import org.koin.compose.navigation3.EntryProvider
import org.koin.core.annotation.KoinExperimentalAPI

private val LocalSharedTransitionScope =
    compositionLocalWithComputedDefaultOf<SharedTransitionScope> {
        error("No shared transition scope provided")
    }

interface Navigator {
    val backStack: List<NavKey>
    val currentScreen: NavKey

    @Composable
    fun getSharedTransitionScope(): SharedTransitionScope

    fun navigateTo(navigationKey: NavKey)
    fun navigateToInclusive(navigationKey: NavKey)
    fun navigateBack(): Boolean

    companion object {
        fun createNavigator(startDestination: NavKey): Navigator =
            DefaultNavigator(startDestination = startDestination)

        @OptIn(KoinExperimentalAPI::class)
        @Composable
        fun createNavDisplay(
            navigator: Navigator,
            entryProvider: EntryProvider<NavKey>,
            modifier: Modifier = Modifier,
        ) {
            SharedTransitionLayout {
                provideSharedTransitionScope {
                    NavDisplay(
                        sharedTransitionScope = this,
                        backStack = navigator.backStack,
                        onBack = { navigator.navigateBack() },
                        entryProvider = entryProvider,
                        entryDecorators = listOf(
                            rememberSaveableStateHolderNavEntryDecorator(),
                            rememberViewModelStoreNavEntryDecorator()
                        ),
                        transitionSpec = {
                            val isFromLogin = initialState.checkType(LoginDestination)
                            val isToHome = targetState.checkType(HomeDestination)
                            val isToLogin = targetState.checkType(LoginDestination)

                            if ((isFromLogin && isToHome) || isToLogin) {
                                fadeIn() togetherWith fadeOut()
                            } else {
                                slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                            }
                        },
                        popTransitionSpec = {
                            val isToLogin = targetState.checkType(LoginDestination)
                            if (isToLogin) {
                                fadeIn() togetherWith fadeOut()
                            } else {
                                slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                            }
                        },
                        predictivePopTransitionSpec = {
                            val isToLogin = targetState.checkType(LoginDestination)
                            if (isToLogin) {
                                fadeIn() togetherWith fadeOut()
                            } else {
                                slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                            }
                        },
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
) : Navigator {
    override val backStack = mutableStateListOf<NavKey>(startDestination)
    override val currentScreen: NavKey by derivedStateOf { backStack.last() }

    @Composable
    override fun getSharedTransitionScope() = LocalSharedTransitionScope.current

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

@Composable
private fun SharedTransitionScope.provideSharedTransitionScope(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalSharedTransitionScope provides this,
        content = content
    )
}

private fun <T: @Serializable NavKey> Scene<NavKey>.checkType(destination: T): Boolean {
    val destinationKey = destination.toString()

    return destinationKey == key.toString()
}
