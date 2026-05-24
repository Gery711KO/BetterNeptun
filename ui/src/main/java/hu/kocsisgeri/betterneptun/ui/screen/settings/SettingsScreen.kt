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
import hu.kocsisgeri.betterneptun.domain.model.localization.Language
import hu.kocsisgeri.betterneptun.localization.localized
import hu.kocsisgeri.betterneptun.ui.BuildConfig
import hu.kocsisgeri.betterneptun.ui.R
import hu.kocsisgeri.betterneptun.ui.core.Navigator
import hu.kocsisgeri.betterneptun.ui.core.theme.BetterNeptunTheme
import hu.kocsisgeri.betterneptun.ui.destination.LoginDestination
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    navigator: Navigator = koinInject(),
) {
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val languages by viewModel.languages.collectAsStateWithLifecycle()
    val notificationDelay by viewModel.notificationDelay.collectAsStateWithLifecycle()

    SettingsContent(
        themeMode = themeMode,
        notificationDelay = notificationDelay,
        languages = languages,
        onThemeChange = viewModel::saveTheme,
        onNotificationDelayChange = viewModel::saveNotificationDelay,
        onLanguageChange = viewModel::changeLanguage,
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
    languages: List<Language>,
    notificationDelay: Int,
    onThemeChange: (ThemeMode) -> Unit,
    onNotificationDelayChange: (Int) -> Unit,
    onLanguageChange: (Language) -> Unit,
    onLogout: () -> Unit,
    onBackClick: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = localized(R.string.settings_title),
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
                .verticalScroll(scrollState)
                .padding(bottom = paddingValues.calculateBottomPadding()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SettingsSectionLabel(label = localized(R.string.settings_section_language))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    languages.forEach { language ->
                        RadioOption(
                            label = localized(language.localizationKey),
                            selected = language.isSelected,
                            onClick = { onLanguageChange(language) }
                        )
                    }
                }
            }

            SettingsSectionLabel(label = localized(R.string.settings_section_theme),)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    RadioOption(
                        label = localized(R.string.settings_section_theme_system),
                        selected = themeMode == ThemeMode.AUTO,
                        onClick = { onThemeChange(ThemeMode.AUTO) }
                    )
                    RadioOption(
                        label = localized(R.string.settings_section_theme_light),
                        selected = themeMode == ThemeMode.LIGHT,
                        onClick = { onThemeChange(ThemeMode.LIGHT) }
                    )
                    RadioOption(
                        label = localized(R.string.settings_section_theme_dark),
                        selected = themeMode == ThemeMode.DARK,
                        onClick = { onThemeChange(ThemeMode.DARK) }
                    )
                }
            }

            SettingsSectionLabel(label = localized(R.string.settings_section_timetable))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    RadioOption(
                        label = localized(R.string.settings_section_timetable_none),
                        selected = notificationDelay == -1,
                        onClick = { onNotificationDelayChange(-1) }
                    )
                    RadioOption(
                        label = localized(R.string.settings_section_timetable_minutes, 5.toString()),
                        selected = notificationDelay == 5,
                        onClick = { onNotificationDelayChange(5) }
                    )
                    RadioOption(
                        label = localized(R.string.settings_section_timetable_minutes, 10.toString()),
                        selected = notificationDelay == 10,
                        onClick = { onNotificationDelayChange(10) }
                    )
                    RadioOption(
                        label = localized(R.string.settings_section_timetable_minutes, 15.toString()),
                        selected = notificationDelay == 15,
                        onClick = { onNotificationDelayChange(15) }
                    )
                    RadioOption(
                        label = localized(R.string.settings_section_timetable_minutes, 30.toString()),
                        selected = notificationDelay == 30,
                        onClick = { onNotificationDelayChange(30) }
                    )
                }
            }

            SettingsSectionLabel(label = localized(R.string.settings_section_information))
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
                        label = localized(R.string.settings_section_information_version),
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
fun RadioOption(
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
                text = localized(R.string.settings_logout_button_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = localized(R.string.settings_logout_button_description),
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
            languages = listOf(
                Language.DEFAULT,
                Language(
                    key = "en",
                    localizationKey = "language_en",
                    isSelected = false,
                    isDefault = false
                )
            ),
            notificationDelay = 15,
            onThemeChange = {},
            onNotificationDelayChange = {},
            onLogout = {},
            onBackClick = {},
            onLanguageChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPreviewDark() {
    BetterNeptunTheme(darkTheme = true) {
        SettingsContent(
            themeMode = ThemeMode.DARK,
            languages = listOf(
                Language.DEFAULT,
                Language(
                    key = "en",
                    localizationKey = "language_en",
                    isSelected = false,
                    isDefault = false
                )
            ),
            notificationDelay = 30,
            onThemeChange = {},
            onNotificationDelayChange = {},
            onLogout = {},
            onBackClick = {},
            onLanguageChange = {}
        )
    }
}
