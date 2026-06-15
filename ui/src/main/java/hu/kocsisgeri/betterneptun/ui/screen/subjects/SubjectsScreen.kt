package hu.kocsisgeri.betterneptun.ui.screen.subjects

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import hu.kocsisgeri.betterneptun.domain.model.UiResult
import hu.kocsisgeri.betterneptun.domain.model.neptun.Subject
import hu.kocsisgeri.betterneptun.localization.LocalizationKey
import hu.kocsisgeri.betterneptun.ui.designsystem.composable.randomTextSize
import hu.kocsisgeri.betterneptun.ui.designsystem.composable.rememberShimmerProgress
import hu.kocsisgeri.betterneptun.ui.designsystem.composable.sharedShimmer
import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
import hu.kocsisgeri.betterneptun.ui.navigation.modifier.sharedBoundsAnimation
import hu.kocsisgeri.betterneptun.ui.screen.subjects.model.SubjectsScreenUiModel
import hu.kocsisgeri.betterneptun.ui.theme.BetterNeptunTheme
import hu.kocsisgeri.betterneptun.ui.theme.PreviewThemeProvider
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import kotlin.random.Random

@Composable
fun SubjectsScreen(
    viewModel: SubjectsViewModel = koinViewModel(),
    navigator: Navigator = koinInject()
) {
    val subjectsState by viewModel.state.collectAsStateWithLifecycle()

    SubjectsContent(
        subjectsState = subjectsState,
        onSelectTerm = viewModel::selectTerm,
        onBackClick = navigator::navigateBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectsContent(
    subjectsState: SubjectsScreenUiModel?,
    onSelectTerm: (String) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        modifier = Modifier.sharedBoundsAnimation(LocalizationKey.HOME_MENU_COURSES),
        topBar = {
            SubjectsScreenTopAppBar(onBackClick)
        },
        containerColor = BetterNeptunTheme.colorScheme.background
    ) { paddingValues ->
        subjectsState?.let {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                FilterItems(
                    selectedTermId = subjectsState.selectedTermId,
                    filterItems = subjectsState.filterBar,
                    onSelectTerm = onSelectTerm
                )
                SubjectsList(
                    listState = subjectsState.listItems
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubjectsScreenTopAppBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "Kurzusok",
                style = BetterNeptunTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold
                )
            )
        },
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
            titleContentColor = BetterNeptunTheme.colorScheme.onBackground,
            navigationIconContentColor = BetterNeptunTheme.colorScheme.onBackground
        )
    )
}

@Composable
private fun FilterItems(
    selectedTermId: String,
    filterItems: UiResult<List<SubjectsScreenUiModel.FilterItem>>?,
    onSelectTerm: (String) -> Unit
) {
    val progress by rememberShimmerProgress(isLoading = filterItems is UiResult.Loading)

    LazyRow(
        contentPadding = PaddingValues(horizontal = BetterNeptunTheme.dimens.screenPadding),
        horizontalArrangement = Arrangement.spacedBy(BetterNeptunTheme.dimens.itemSpacing)
    ) {
        when (filterItems) {
            is UiResult.Success<List<SubjectsScreenUiModel.FilterItem>> -> {
                items(filterItems.data.asReversed()) { item ->
                    FilterChip(
                        selected = item.id == selectedTermId,
                        onClick = { onSelectTerm(item.id) },
                        label = {
                            Text(
                                text = item.name,
                                style = BetterNeptunTheme.typography.bodyMedium
                            )
                        }
                    )
                }
            }

            is UiResult.Loading -> {
                items(Random.nextInt(5, 10)) {
                    Box(
                        modifier = Modifier
                            .sharedShimmer(progress)
                            .padding(BetterNeptunTheme.dimens.small)
                    ) {
                        Text(
                            text = randomTextSize(),
                            style = BetterNeptunTheme.typography.bodyMedium
                        )
                    }
                }
            }

            null -> Unit
        }
    }
}

@Composable
private fun SubjectsList(listState: UiResult<List<Subject>>?) {
    val loadingProgress = rememberShimmerProgress(isLoading = listState is UiResult.Loading)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(BetterNeptunTheme.dimens.screenPadding)
            .clip(BetterNeptunTheme.shapes.large),
        verticalArrangement = Arrangement.spacedBy(BetterNeptunTheme.dimens.itemSpacing)
    ) {
        when (listState) {
            is UiResult.Loading -> items(1) {
                SubjectItemLoading(loadingProgress)
            }

            is UiResult.Success -> items(listState.data) { subject ->
                SubjectItem(subject = subject)
            }

            null -> {}
        }
    }
}

@Composable
fun SubjectItem(subject: Subject) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val rotation by animateFloatAsState(targetValue = if (expanded) 180f else 0f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = BetterNeptunTheme.shapes.large,
        colors = CardDefaults.elevatedCardColors(
            containerColor = BetterNeptunTheme.colorScheme.surfaceVariant
        ),
        onClick = { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier.padding(BetterNeptunTheme.dimens.screenPadding)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier.size(BetterNeptunTheme.dimens.large),
                    contentAlignment = Alignment.Center
                ) {
                    if (subject.isCompleted) {
                        Icon(
                            imageVector = Icons.Rounded.Done,
                            contentDescription = "Teljesítve",
                            tint = BetterNeptunTheme.colorScheme.tertiary,
                            modifier = Modifier.size(BetterNeptunTheme.dimens.iconLarge)
                        )
                    } else {
                        Surface(
                            shape = BetterNeptunTheme.shapes.small,
                            color = BetterNeptunTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(BetterNeptunTheme.dimens.badgeSize)
                        ) {}
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = BetterNeptunTheme.dimens.itemSpacing),
                    verticalArrangement = Arrangement.spacedBy(BetterNeptunTheme.dimens.small)
                ) {
                    Text(
                        text = subject.subjectName,
                        style = BetterNeptunTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = BetterNeptunTheme.colorScheme.onSurface
                    )
                    Text(
                        text = subject.subjectCode,
                        style = BetterNeptunTheme.typography.bodySmall,
                        color = BetterNeptunTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = null,
                    modifier = Modifier
                        .size(BetterNeptunTheme.dimens.iconMedium)
                        .rotate(rotation),
                    tint = BetterNeptunTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .padding(top = BetterNeptunTheme.dimens.medium)
                        .fillMaxWidth()
                ) {
                    HorizontalDivider(
                        modifier = Modifier.padding(bottom = BetterNeptunTheme.dimens.itemSpacing),
                        thickness = BetterNeptunTheme.dimens.dividerThickness,
                        color = BetterNeptunTheme.colorScheme.outlineVariant
                    )

                    DetailItem(label = "Kredit", value = subject.subjectCredit.toString())
                    DetailItem(label = "Követelmény", value = subject.subjectRequirement)
                    DetailItem(label = "Tárgytípus", value = "Kötelezően választott")
                }
            }
        }
    }
}

@Composable
fun SubjectItemLoading(progress: State<Float>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = BetterNeptunTheme.shapes.large,
        colors = CardDefaults.elevatedCardColors(
            containerColor = BetterNeptunTheme.colorScheme.surfaceVariant
        ),
    ) {
        Column(modifier = Modifier.padding(BetterNeptunTheme.dimens.screenPadding)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier.size(BetterNeptunTheme.dimens.large),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(BetterNeptunTheme.dimens.badgeSize)
                            .clip(BetterNeptunTheme.shapes.small)
                            .sharedShimmer(progress.value)
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = BetterNeptunTheme.dimens.itemSpacing),
                    verticalArrangement = Arrangement.spacedBy(BetterNeptunTheme.dimens.small)
                ) {
                    Text(
                        text = randomTextSize(),
                        style = BetterNeptunTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = BetterNeptunTheme.colorScheme.onSurface,
                        modifier = Modifier.sharedShimmer(progress.value),
                    )
                    Text(
                        text = randomTextSize(),
                        style = BetterNeptunTheme.typography.bodySmall,
                        color = BetterNeptunTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.sharedShimmer(progress.value),
                    )
                }

                Box(
                    modifier = Modifier
                        .size(BetterNeptunTheme.dimens.iconExtraLarge)
                        .sharedShimmer(progress.value)
                )
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = BetterNeptunTheme.dimens.extraSmall),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = BetterNeptunTheme.typography.labelLarge,
            color = BetterNeptunTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = BetterNeptunTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = BetterNeptunTheme.colorScheme.onSurface,
            textAlign = TextAlign.End
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SubjectsScreenSuccessPreview() {
    BetterNeptunTheme {
        SubjectsContent(
            subjectsState = SubjectsScreenUiModel(
                selectedTermId = "1",
                filterBar = UiResult.Success(
                    listOf(
                        SubjectsScreenUiModel.FilterItem(
                            id = "1",
                            name = "2023/24/1",
                        ),
                        SubjectsScreenUiModel.FilterItem(
                            id = "2",
                            name = "2023/24/2",
                        )
                    )
                ),
                listItems = UiResult.Success(
                    listOf(
                        Subject(
                            subjectId = "1",
                            subjectCode = "GKNB_INTM001",
                            subjectCredit = 5,
                            subjectName = "Programozás I.",
                            subjectRequirement = "Vizsga",
                            termId = "2023/24/1",
                            isCompleted = true
                        ),
                        Subject(
                            subjectId = "2",
                            subjectCode = "GKNB_INTM002",
                            subjectCredit = 3,
                            subjectName = "Diszkrét matematika",
                            subjectRequirement = "Aláírás",
                            termId = "2023/24/1",
                            isCompleted = false
                        )
                    )
                )
            ),
            onSelectTerm = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SubjectsScreenLoadingPreview() {
    BetterNeptunTheme {
        SubjectsContent(
            subjectsState = SubjectsScreenUiModel(
                selectedTermId = "1",
                filterBar = UiResult.Loading,
                listItems = UiResult.Loading
            ),
            onSelectTerm = {},
            onBackClick = {}
        )
    }
}

@PreviewLightDark
@PreviewWrapper(PreviewThemeProvider::class)
@Composable
private fun SubjectItemPreview() {
    Box(modifier = Modifier.padding(16.dp)) {
        SubjectItem(
            subject = Subject(
                subjectId = "1",
                subjectCode = "GKNB_INTM001",
                subjectCredit = 5,
                subjectName = "Programozás I.",
                subjectRequirement = "Vizsga",
                termId = "2023/24/1",
                isCompleted = true
            )
        )
    }
}
