package hu.kocsisgeri.betterneptun.ui.screen.loading

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import hu.kocsisgeri.betterneptun.domain.initializable.Initializer
import hu.kocsisgeri.betterneptun.ui.R
import hu.kocsisgeri.betterneptun.ui.core.Navigator
import hu.kocsisgeri.betterneptun.ui.destination.LoginDestination
import org.koin.compose.koinInject

@Composable
fun LoadingScreen(
    navigator: Navigator = koinInject(),
    initializer: Initializer = koinInject()
) {
    LaunchedEffect(Unit) {
        initializer.initialize(this)

        initializer.isInitialized.collect {
            if (it) navigator.navigateToInclusive(LoginDestination)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Image(
                painter = painterResource(id = R.drawable.oe_logo),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                modifier = Modifier.width(120.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}