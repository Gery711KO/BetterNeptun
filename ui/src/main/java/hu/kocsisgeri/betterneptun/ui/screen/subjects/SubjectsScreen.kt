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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import hu.kocsisgeri.betterneptun.ui.core.theme.BetterNeptunTheme
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import hu.kocsisgeri.betterneptun.domain.model.neptun.ApiResult
import hu.kocsisgeri.betterneptun.domain.model.neptun.Subject
import hu.kocsisgeri.betterneptun.localization.LocalizationKey
import hu.kocsisgeri.betterneptun.ui.core.modifier.sharedBoundsAnimation
import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
import hu.kocsisgeri.betterneptun.ui.screen.subjects.model.SubjectsScreenUiModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

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
            Column (modifier = Modifier.fillMaxSize().padding(paddingValues)) {
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
    filterItems: ApiResult<List<SubjectsScreenUiModel.FilterItem>>?,
    onSelectTerm: (String) -> Unit
) {
    when (filterItems) {
        is ApiResult.Error -> Unit
        ApiResult.Loading -> Unit
        is ApiResult.Success<List<SubjectsScreenUiModel.FilterItem>> -> {
            LazyRow(
                contentPadding = PaddingValues(horizontal = BetterNeptunTheme.dimens.screenPadding),
                horizontalArrangement = Arrangement.spacedBy(BetterNeptunTheme.dimens.itemSpacing)
            ) {
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
        }

        null -> Unit
    }
}

@Composable
private fun SubjectsList(listState: ApiResult<List<Subject>>?) {
    when (listState) {
        is ApiResult.Loading -> {
            Box(Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        is ApiResult.Success -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(BetterNeptunTheme.dimens.screenPadding)
                    .clip(BetterNeptunTheme.shapes.large),
                verticalArrangement = Arrangement.spacedBy(BetterNeptunTheme.dimens.itemSpacing)
            ) {
                items(listState.data) { subject ->
                    SubjectItem(subject = subject)
                }
            }
        }

        is ApiResult.Error -> {
            Box(Modifier.fillMaxSize()) {
                Text(
                    text = "Hiba történt az adatok betöltésekor",
                    color = BetterNeptunTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }

        null -> {}
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
                filterBar = ApiResult.Success(
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
                listItems = ApiResult.Success(
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
                filterBar = ApiResult.Loading,
                listItems = ApiResult.Loading
            ),
            onSelectTerm = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SubjectsScreenErrorPreview() {
    BetterNeptunTheme {
        SubjectsContent(
            subjectsState = SubjectsScreenUiModel(
                selectedTermId = "1",
                filterBar = ApiResult.Error("Hiba"),
                listItems = ApiResult.Error("Hiba")
            ),
            onSelectTerm = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SubjectItemPreview() {
    BetterNeptunTheme {
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
}
