package hu.kocsisgeri.betterneptun.ui.screen

import androidx.fragment.app.Fragment
import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
import org.koin.android.ext.android.inject

abstract class ComposeFragment: Fragment() {

    val navigator: Navigator by inject()
}