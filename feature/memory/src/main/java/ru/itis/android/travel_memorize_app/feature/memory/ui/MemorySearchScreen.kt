package ru.itis.android.travel_memorize_app.feature.memory.ui

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import ru.itis.android.travel_memorize_app.core.domain.model.map.PlaceMark
import ru.itis.android.travel_memorize_app.core.ui.theme.LocalExtendedColors
import ru.itis.android.travel_memorize_app.feature.memory.viewmodel.MemorySearchViewModel
import ru.itis.android.travel_memorize_app.ui.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MemorySearchScreen(
    viewModelFactory: ViewModelProvider.Factory,
    onBack: () -> Unit,
    onMemoryClick: (String) -> Unit
) {
    val viewModel: MemorySearchViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.state.collectAsState()
    val colors = LocalExtendedColors.current
    val configuration = LocalConfiguration.current
    val locale: Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        configuration.locales[0]
    } else {
        @Suppress("DEPRECATION")
        configuration.locale
    }
    Scaffold(
        topBar = {
            MemorySearchTopBar(
                onClear = viewModel::clearQuery,
                onDone = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            MemorySearchField(
                query = state.query,
                onQueryChange = viewModel::updateQuery
            )


            Spacer(modifier = Modifier.height(16.dp))
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }

                }


                state.filteredMemories.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.memory_search_empty),
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.secondaryText
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(
                            items = state.filteredMemories,
                            key = { it.id }
                        ) { memory ->
                            MemorySearchItem(
                                memory = memory,
                                locale = locale,
                                onClick = { onMemoryClick(memory.id) }
                            )
                        }

                    }
                }
            }
        }


    }
}

@Composable
private fun MemorySearchTopBar(
    onClear: () -> Unit,
    onDone: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        TextButton(
            onClick = onClear,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Text(text = stringResource(R.string.clear))
        }
        Text(
            text = stringResource(R.string.memory_search_title),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        TextButton(
            onClick = onDone,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Text(text = stringResource(R.string.done))
        }
    }

}

@Composable
private fun MemorySearchField(
    query: String,
    onQueryChange: (String) -> Unit
) {
    val colors = LocalExtendedColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(26.dp)
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = colors.inputMuted,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions.Default,
            modifier = Modifier.weight(1f),
            decorationBox = { innerTextField ->
                if (query.isBlank()) {
                    Text(
                        text = stringResource(R.string.nav_map_search_placeholder),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.inputMuted
                    )
                }
                innerTextField()
            }
        )
        if (query.isNotBlank()) {
            IconButton(
                onClick = { onQueryChange("") },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = colors.inputMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun MemorySearchItem(
    memory: PlaceMark,
    locale: Locale,
    onClick: () -> Unit
) {
    val colors = LocalExtendedColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val photoUrl = memory.photos.firstOrNull()

        if (photoUrl.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(colors.inputContainer, CircleShape)
            )
        } else {
            AsyncImage(
                model = photoUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = memory.title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            Text(
                text = listOfNotNull(memory.country, memory.city)
                    .joinToString(", ")
                    .ifBlank { memory.placeName.orEmpty() },
                style = MaterialTheme.typography.bodySmall,
                color = colors.secondaryText,
                maxLines = 1
            )
            memory.visitDate?.let {
                Text(
                    text = SimpleDateFormat("d MMM yyyy", locale).format(Date(it)),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.secondaryText,
                    maxLines = 1
                )
            }
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = colors.inputMuted,
            modifier = Modifier.size(22.dp)
        )
    }


}