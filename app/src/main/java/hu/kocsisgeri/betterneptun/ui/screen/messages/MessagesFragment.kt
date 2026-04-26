package hu.kocsisgeri.betterneptun.ui.screen.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import hu.kocsisgeri.betterneptun.ui.navigation.destination.MessageDetailDestination
import hu.kocsisgeri.betterneptun.ui.screen.ComposeFragment
import hu.kocsisgeri.betterneptun.ui.theme.BetterNeptunTheme
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class MessagesFragment : ComposeFragment() {

    private val viewModel: MessagesViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                BetterNeptunTheme {
                    MessagesScreen(
                        viewModel = viewModel,
                        onBackClick = { navigator.navigateBack() },
                        onMessageClick = { navigator.navigateTo(MessageDetailDestination) }
                    )
                }
            }
        }
    }
}
