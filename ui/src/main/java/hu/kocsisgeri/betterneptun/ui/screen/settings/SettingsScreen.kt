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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import hu.kocsisgeri.betterneptun.domain.model.ThemeMode
import hu.kocsisgeri.betterneptun.domain.model.localization.Language
import hu.kocsisgeri.betterneptun.localization.LocalizationKey
import hu.kocsisgeri.betterneptun.localization.localized
import hu.kocsisgeri.betterneptun.ui.BuildConfig
import hu.kocsisgeri.betterneptun.ui.theme.BetterNeptunTheme
import hu.kocsisgeri.betterneptun.ui.theme.PreviewThemeProvider
import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
import hu.kocsisgeri.betterneptun.ui.navigation.modifier.sharedBoundsAnimation
import hu.kocsisgeri.betterneptun.ui.screen.settings.model.SettingsRadioOption
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
        onLogout = viewModel::logout,
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
        topBar = {
            SettingsScreenTopAppBar(
                scrollBehavior = scrollBehavior,
                onBackClick = onBackClick
            )
        },
        containerColor = BetterNeptunTheme.colorScheme.background,
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .sharedBoundsAnimation(LocalizationKey.SETTINGS_TITLE)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
                .padding(horizontal = BetterNeptunTheme.dimens.screenPadding)
                .clip(BetterNeptunTheme.shapes.large)
                .verticalScroll(scrollState)
                .padding(bottom = paddingValues.calculateBottomPadding()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ChangeableSection(
                languages = languages,
                onLanguageChange = onLanguageChange,
                themeMode = themeMode,
                onThemeChange = onThemeChange,
                notificationDelay = notificationDelay,
                onNotificationDelayChange = onNotificationDelayChange
            )
            InfoSection()
            Spacer(modifier = Modifier.height(BetterNeptunTheme.dimens.extraLarge))
            LogoutButton(onClick = onLogout)
            Spacer(modifier = Modifier.height(BetterNeptunTheme.dimens.extraLarge))
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SettingsScreenTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onBackClick: () -> Unit
) {
    LargeTopAppBar(
        title = {
            Text(
                text = LocalizationKey.SETTINGS_TITLE.localized(),
                style = BetterNeptunTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
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
            containerColor = BetterNeptunTheme.colorScheme.background,
            scrolledContainerColor = BetterNeptunTheme.colorScheme.background,
            navigationIconContentColor = BetterNeptunTheme.colorScheme.onSurface,
            titleContentColor = BetterNeptunTheme.colorScheme.onSurface,
        )
    )
}

@Composable
private fun ChangeableSection(
    languages: List<Language>,
    onLanguageChange: (Language) -> Unit,
    themeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    notificationDelay: Int,
    onNotificationDelayChange: (Int) -> Unit
) {
    SettingsSection(
        sectionTitle = LocalizationKey.SETTINGS_SECTION_LANGUAGE,
        radioOptions = languages.map { language ->
            SettingsRadioOption(
                label = localized(language.localizationKey),
                isSelected = language.isSelected,
                onClick = { onLanguageChange(language) }
            )
        },
    )

    SettingsSection(
        sectionTitle = LocalizationKey.SETTINGS_SECTION_THEME,
        radioOptions = ThemeMode.entries.map {
            SettingsRadioOption(
                label = when (it) {
                    ThemeMode.AUTO -> LocalizationKey.SETTINGS_SECTION_THEME_SYSTEM
                    ThemeMode.DARK -> LocalizationKey.SETTINGS_SECTION_THEME_DARK
                    ThemeMode.LIGHT -> LocalizationKey.SETTINGS_SECTION_THEME_LIGHT
                }.localized(),
                isSelected = it == themeMode,
                onClick = { onThemeChange(it) }
            )
        }
    )

    SettingsSection(
        sectionTitle = LocalizationKey.SETTINGS_SECTION_TIMETABLE,
        radioOptions = buildList {
            repeat(5) { index ->
                when (index) {
                    0 -> add(
                        SettingsRadioOption(
                            label = LocalizationKey.SETTINGS_SECTION_TIMETABLE_NONE.localized(),
                            isSelected = notificationDelay == -1,
                            onClick = { onNotificationDelayChange(-1) }
                        )
                    )

                    else -> {
                        val delay = (index * 6)
                        add(
                            SettingsRadioOption(
                                label = LocalizationKey.SETTINGS_SECTION_TIMETABLE_MINUTES(
                                    delay.toString()
                                ).localized(),
                                isSelected = delay == notificationDelay,
                                onClick = { onNotificationDelayChange(delay) }
                            )
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun SettingsSection(
    sectionTitle: LocalizationKey,
    radioOptions: List<SettingsRadioOption>,
) {
    SettingsSectionLabel(label = sectionTitle.localized())
    Card(
        shape = BetterNeptunTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = BetterNeptunTheme.colorScheme.surfaceVariant,
            contentColor = BetterNeptunTheme.colorScheme.onSurfaceVariant
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(vertical = BetterNeptunTheme.dimens.paddingSmall)) {
            radioOptions.forEach { option ->
                RadioOption(
                    label = option.label,
                    selected = option.isSelected,
                    onClick = option.onClick
                )
            }
        }
    }
}

@Composable
fun SettingsSectionLabel(label: String) {
    Text(
        text = label,
        style = BetterNeptunTheme.typography.bodyMedium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = BetterNeptunTheme.dimens.paddingExtraLarge, bottom = BetterNeptunTheme.dimens.paddingSmall),
        color = BetterNeptunTheme.colorScheme.onBackground
    )
}

@Composable
private fun InfoSection() {
    SettingsSectionLabel(label = LocalizationKey.SETTINGS_SECTION_INFORMATION.localized())
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = BetterNeptunTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = BetterNeptunTheme.colorScheme.surfaceVariant,
            contentColor = BetterNeptunTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(BetterNeptunTheme.dimens.paddingMedium)) {
            InfoRow(
                label = LocalizationKey.SETTINGS_SECTION_INFORMATION_VERSION.localized(),
                value = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
            )
        }
    }
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
            .padding(horizontal = BetterNeptunTheme.dimens.paddingExtraLarge, vertical = BetterNeptunTheme.dimens.itemSpacing),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = BetterNeptunTheme.typography.bodyLarge.copy(
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (selected) {
                BetterNeptunTheme.colorScheme.onSurface
            } else {
                BetterNeptunTheme.colorScheme.onSurfaceVariant
            }
        )
        RadioButton(
            selected = selected,
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = BetterNeptunTheme.colorScheme.primary,
                unselectedColor = BetterNeptunTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = BetterNeptunTheme.dimens.itemSpacing),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = BetterNeptunTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = BetterNeptunTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = BetterNeptunTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = BetterNeptunTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun LogoutButton(onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(0.7f),
        shape = BetterNeptunTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = BetterNeptunTheme.colorScheme.errorContainer,
            contentColor = BetterNeptunTheme.colorScheme.onErrorContainer
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .padding(BetterNeptunTheme.dimens.paddingMedium)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = LocalizationKey.SETTINGS_LOGOUT_BUTTON_TITLE.localized(),
                style = BetterNeptunTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = LocalizationKey.SETTINGS_LOGOUT_BUTTON_DESCRIPTION.localized(),
                style = BetterNeptunTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@PreviewWrapper(PreviewThemeProvider::class)
@Composable
fun SettingsPreviewLight() {
    SettingsContent(
        themeMode = ThemeMode.AUTO,
        languages = listOf(
            Language.DEFAULT,
            Language(
                key = "en",
                localizationKey = LocalizationKey.LANGUAGE_EN.key,
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

@Preview
@PreviewWrapper(PreviewThemeProvider::class)
@Composable
fun SettingsPreviewDark() {
    SettingsContent(
        themeMode = ThemeMode.DARK,
        languages = listOf(
            Language.DEFAULT,
            Language(
                key = "en",
                localizationKey = LocalizationKey.LANGUAGE_EN.key,
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
