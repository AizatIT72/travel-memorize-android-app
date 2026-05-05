package ru.itis.android.travel_memorize_app.feature.memory.ui

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import kotlinx.coroutines.flow.collectLatest
import ru.itis.android.travel_memorize_app.core.domain.model.map.PlaceMark
import ru.itis.android.travel_memorize_app.core.ui.theme.LocalExtendedColors
import ru.itis.android.travel_memorize_app.feature.memory.viewmodel.MemoriesEffect
import ru.itis.android.travel_memorize_app.feature.memory.viewmodel.MemoriesLayoutMode
import ru.itis.android.travel_memorize_app.feature.memory.viewmodel.MemoriesSortMode
import ru.itis.android.travel_memorize_app.feature.memory.viewmodel.MemoriesViewModel
import ru.itis.android.travel_memorize_app.ui.R
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun MemoriesScreen(
    viewModelFactory: ViewModelProvider.Factory,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    onAddClick: () -> Unit,
    onMemoryClick: (String) -> Unit
) {
    val viewModel: MemoriesViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var errorResId by remember { mutableStateOf<Int?>(null) }
    var showSortMenu by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        viewModel.loadMemories()
    }
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is MemoriesEffect.ShowError -> errorResId = effect.error.toMemoryMessageRes()
            }
        }
    }

    errorResId?.let { resId ->
        val message = stringResource(resId)
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(message)
            errorResId = null
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(bottom = paddingValues.calculateBottomPadding())
                    .size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.button_add_memory)
                )

            }

        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(bottom = paddingValues.calculateBottomPadding())
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                MemoriesTopBar()
                Spacer(Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.memories_feed_title),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f)
                    )
                    Box {
                        IconButton(
                            onClick = { showSortMenu = true },
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = CircleShape

                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sort,
                                contentDescription = stringResource(R.string.sort_memories),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            SortItem(
                                text = stringResource(R.string.sort_newest_first),
                                selected = state.sortMode == MemoriesSortMode.NEWEST_FIRST,
                                onClick = {
                                    viewModel.updateSortMode(MemoriesSortMode.NEWEST_FIRST)
                                    showSortMenu = false
                                }
                            )


                            SortItem(
                                text = stringResource(R.string.sort_oldest_first),
                                selected = state.sortMode == MemoriesSortMode.OLDEST_FIRST,
                                onClick = {
                                    viewModel.updateSortMode(MemoriesSortMode.OLDEST_FIRST)
                                    showSortMenu = false
                                }
                            )
                            SortItem(
                                text = stringResource(R.string.sort_title_asc),
                                selected = state.sortMode == MemoriesSortMode.TITLE_ASC,
                                onClick = {
                                    viewModel.updateSortMode(MemoriesSortMode.TITLE_ASC)
                                    showSortMenu = false
                                }
                            )
                            SortItem(
                                text = stringResource(R.string.sort_title_desc),
                                selected = state.sortMode == MemoriesSortMode.TITLE_DESC,
                                onClick = {
                                    viewModel.updateSortMode(MemoriesSortMode.TITLE_DESC)
                                    showSortMenu = false
                                }
                            )
                        }
                    }

                    Spacer(Modifier.width(8.dp))
                    IconButton(
                        onClick = viewModel::toggleLayoutMode,
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = when (state.layoutMode) {
                                MemoriesLayoutMode.LIST -> Icons.Default.GridView
                                MemoriesLayoutMode.GRID -> Icons.Default.ViewAgenda
                            },
                            contentDescription = stringResource(R.string.change_layout),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))
                when {
                    state.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    state.sortedMemories.isEmpty() -> {
                        EmptyMemoriesState(
                            onAddClick = onAddClick,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    state.layoutMode == MemoriesLayoutMode.LIST -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(18.dp),
                            contentPadding = PaddingValues(bottom = 104.dp)
                        ) {
                            items(
                                items = state.sortedMemories,
                                key = { it.id }
                            ) { memory ->
                                MemoryListCard(
                                    memory = memory,
                                    onClick = { onMemoryClick(memory.id) }
                                )
                            }
                        }
                    }
                    state.layoutMode == MemoriesLayoutMode.GRID -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(18.dp),
                            contentPadding = PaddingValues(bottom = 104.dp)
                        ) {
                            items(
                                items = state.sortedMemories,
                                key = { it.id }
                            ) { memory ->
                                MemoryGridCard(
                                    memory = memory,
                                    onClick = { onMemoryClick(memory.id) }
                                )
                            }
                        }

                    }
                }
            }


        }
    }


}

@Composable
private fun MemoriesTopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.memories),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun SortItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Text(
                text = text,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        },
        onClick = onClick
    )
}

@Composable
private fun MemoryListCard(
    memory: PlaceMark,
    onClick: () -> Unit
) {
    val colors = LocalExtendedColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = memory.photos.firstOrNull(),
            contentDescription = memory.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(184.dp)
        )
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = memory.title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(4.dp))
            Text(
                text = memory.visitDate.formatMemoryDate(),
                style = MaterialTheme.typography.bodySmall,
                color = colors.secondaryText
            )
        }
    }
}

@Composable
private fun MemoryGridCard(
    memory: PlaceMark,
    onClick: () -> Unit
) {
    val colors = LocalExtendedColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = memory.photos.firstOrNull(),
            contentDescription = memory.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        )
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = memory.title,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = memory.visitDate.formatMemoryDate(),
                style = MaterialTheme.typography.bodySmall,
                color = colors.secondaryText,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun EmptyMemoriesState(
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.empty_memories_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.empty_memories_message),
            style = MaterialTheme.typography.bodyMedium,
            color = LocalExtendedColors.current.secondaryText
        )

        Spacer(Modifier.height(20.dp))
        Button(onClick = onAddClick) {
            Text(stringResource(R.string.button_add_memory))
        }
    }
}

@Composable
private fun Long?.formatMemoryDate(): String {
    if (this == null) return stringResource(R.string.date_unknown)
    val configuration = LocalConfiguration.current
    val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        configuration.locales[0]
    } else {
        @Suppress("DEPRECATION")
        configuration.locale
    }

    return remember(this, locale) {
        SimpleDateFormat("d MMM yyyy", locale).format(Date(this))
    }
}