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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import hu.kocsisgeri.betterneptun.ui.core.Navigator
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
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Kurzusok",
                        style = MaterialTheme.typography.titleLarge.copy(
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
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        subjectsState?.let {
            Column (
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
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filterItems.data.asReversed()) { item ->
                    FilterChip(
                        selected = item.id == selectedTermId,
                        onClick = { onSelectTerm(item.id) },
                        label = {
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.bodyMedium
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
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
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
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }

        null -> {}
    }
}

@Composable
fun SubjectItem(subject: Subject) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(targetValue = if (expanded) 180f else 0f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        onClick = { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier.size(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (subject.isCompleted) {
                        Icon(
                            imageVector = Icons.Rounded.Done,
                            contentDescription = "Teljesítve",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(28.dp)
                        )
                    } else {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(8.dp)
                        ) {}
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = subject.subjectName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = subject.subjectCode,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(rotation),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                ) {
                    HorizontalDivider(
                        modifier = Modifier.padding(bottom = 12.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
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
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
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
