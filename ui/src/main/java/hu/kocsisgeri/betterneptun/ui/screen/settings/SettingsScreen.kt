package hu.kocsisgeri.betterneptun.ui.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import hu.kocsisgeri.betterneptun.domain.model.ThemeMode
import hu.kocsisgeri.betterneptun.ui.BuildConfig
import hu.kocsisgeri.betterneptun.ui.core.Navigator
import hu.kocsisgeri.betterneptun.ui.destination.LoginDestination
import hu.kocsisgeri.betterneptun.ui.core.theme.BetterNeptunTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    navigator: Navigator = koinInject(),
) {
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val notificationDelay by viewModel.notificationDelay.collectAsStateWithLifecycle()

    SettingsContent(
        themeMode = themeMode,
        notificationDelay = notificationDelay,
        onThemeChange = viewModel::saveTheme,
        onNotificationDelayChange = viewModel::saveNotificationDelay,
        onLogout = {
            viewModel.logout()
            navigator.navigateToInclusive(LoginDestination)
        },
        onBackClick = navigator::navigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    themeMode: ThemeMode,
    notificationDelay: Int,
    onThemeChange: (ThemeMode) -> Unit,
    onNotificationDelayChange: (Int) -> Unit,
    onLogout: () -> Unit,
    onBackClick: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Beállítások",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    )
                },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Vissza"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
                .padding(horizontal = 16.dp)
                .clip(MaterialTheme.shapes.large)
                .verticalScroll(rememberScrollState())
                .padding(bottom = paddingValues.calculateBottomPadding()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SettingsSectionLabel(label = "Megjelenés")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    ThemeOption(
                        label = "Automatikus",
                        selected = themeMode == ThemeMode.AUTO,
                        onClick = { onThemeChange(ThemeMode.AUTO) }
                    )
                    ThemeOption(
                        label = "Világos mód",
                        selected = themeMode == ThemeMode.LIGHT,
                        onClick = { onThemeChange(ThemeMode.LIGHT) }
                    )
                    ThemeOption(
                        label = "Sötét mód",
                        selected = themeMode == ThemeMode.DARK,
                        onClick = { onThemeChange(ThemeMode.DARK) }
                    )
                }
            }

            SettingsSectionLabel(label = "Naptár")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    DelayOption(
                        label = "Nincs értesítés",
                        selected = notificationDelay == -1,
                        onClick = { onNotificationDelayChange(-1) }
                    )
                    DelayOption(
                        label = "5 perccel előtte",
                        selected = notificationDelay == 5,
                        onClick = { onNotificationDelayChange(5) }
                    )
                    DelayOption(
                        label = "10 perccel előtte",
                        selected = notificationDelay == 10,
                        onClick = { onNotificationDelayChange(10) }
                    )
                    DelayOption(
                        label = "15 perccel előtte",
                        selected = notificationDelay == 15,
                        onClick = { onNotificationDelayChange(15) }
                    )
                    DelayOption(
                        label = "30 perccel előtte",
                        selected = notificationDelay == 30,
                        onClick = { onNotificationDelayChange(30) }
                    )
                }
            }

            SettingsSectionLabel(label = "Információk")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    InfoRow(
                        label = "Verzió",
                        value = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            LogoutButton(onClick = onLogout)

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun SettingsSectionLabel(label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 8.dp),
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun DelayOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (selected) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
        RadioButton(
            selected = selected,
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary,
                unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

@Composable
fun ThemeOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (selected) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
        RadioButton(
            selected = selected,
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary,
                unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun LogoutButton(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Kilépés",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = "Minden elmentett adat törlése és kijelentkezés",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPreviewLight() {
    BetterNeptunTheme(darkTheme = false) {
        SettingsContent(
            themeMode = ThemeMode.AUTO,
            notificationDelay = 15,
            onThemeChange = {},
            onNotificationDelayChange = {},
            onLogout = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPreviewDark() {
    BetterNeptunTheme(darkTheme = true) {
        SettingsContent(
            themeMode = ThemeMode.DARK,
            notificationDelay = 30,
            onThemeChange = {},
            onNotificationDelayChange = {},
            onLogout = {},
            onBackClick = {}
        )
    }
}
