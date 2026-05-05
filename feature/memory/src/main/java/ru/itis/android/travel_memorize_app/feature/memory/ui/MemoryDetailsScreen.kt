package ru.itis.android.travel_memorize_app.feature.memory.ui

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import kotlinx.coroutines.flow.collectLatest
import ru.itis.android.travel_memorize_app.core.ui.components.BackButton
import ru.itis.android.travel_memorize_app.core.ui.components.CustomButton
import ru.itis.android.travel_memorize_app.core.ui.components.CustomCard
import ru.itis.android.travel_memorize_app.core.ui.theme.LocalExtendedColors
import ru.itis.android.travel_memorize_app.feature.memory.viewmodel.MemoryDetailsEffect
import ru.itis.android.travel_memorize_app.feature.memory.viewmodel.MemoryDetailsViewModel
import ru.itis.android.travel_memorize_app.ui.R
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MemoryDetailsScreen(
    viewModelFactory: ViewModelProvider.Factory,
    memoryId: String,
    onBack: () -> Unit,
    onEditClick: ((String) -> Unit)? = null,
    onDeleted: (() -> Unit)? = null,
    showOwnerActions: Boolean = true
) {
    val viewModel: MemoryDetailsViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var errorResId by remember { mutableStateOf<Int?>(null) }
    val colors = LocalExtendedColors.current
    val configuration = LocalConfiguration.current
    val locale: Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        configuration.locales[0]
    } else {
        @Suppress("DEPRECATION")
        configuration.locale
    }
    LaunchedEffect(memoryId) {
        viewModel.loadMemory(memoryId)
    }
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is MemoryDetailsEffect.ShowError -> errorResId = effect.error.toMemoryMessageRes()
                MemoryDetailsEffect.Deleted -> onDeleted?.invoke()
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
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                state.memory == null -> {
                    Text(
                        text = stringResource(R.string.memory_not_found),
                        modifier = Modifier.align(Alignment.Center),
                        color = colors.secondaryText
                    )
                }
                else -> {
                    val memory = state.memory!!
                    val photos = memory.photos
                    var selectedPhotoIndex by remember { mutableStateOf(0) }
                    var showDeleteDialog by remember { mutableStateOf(false) }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {


                        Box {
                            AsyncImage(
                                model = photos.getOrNull(selectedPhotoIndex),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(320.dp)
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.18f))
                            )
                            BackButton(
                                onClick = onBack,
                                modifier = Modifier
                                    .padding(start = 16.dp, top = 16.dp)
                            )
                            Text(
                                text = stringResource(R.string.memory_details_title),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 26.dp)
                            )
                        }


                        CustomCard(
                            modifier = Modifier
                                .offset(y = (-48).dp)
                                .padding(horizontal = 24.dp)
                                .fillMaxWidth()
                        ) {

                            Column {
                                Text(
                                    text = listOfNotNull(memory.country, memory.city)
                                        .joinToString(", ")
                                        .ifBlank { memory.placeName.orEmpty() }
                                        .uppercase(),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(Modifier.height(12.dp))

                                Text(
                                    text = memory.title,
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(Modifier.height(16.dp))

                                memory.visitDate?.let {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CalendarMonth,
                                            contentDescription = null,
                                            tint = colors.secondaryText,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            text = SimpleDateFormat("d MMM yyyy", locale).format(Date(it)),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = colors.secondaryText
                                        )
                                    }

                                    Spacer(Modifier.height(12.dp))
                                }
                                memory.placeName?.let {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Place,
                                            contentDescription = null,
                                            tint = colors.secondaryText,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            text = it,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = colors.secondaryText
                                        )

                                    }
                                    Spacer(Modifier.height(20.dp))
                                }
                                Text(
                                    text = memory.description
                                        ?.takeIf { it.isNotBlank() }
                                        ?: stringResource(R.string.empty_story),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )


                                Spacer(Modifier.height(28.dp))

                                if (showOwnerActions) {
                                    CustomButton(
                                        text = stringResource(R.string.edit_memory),
                                        enabled = true,
                                        onClick = { onEditClick?.invoke(memory.id) }
                                    )

                                    TextButton(
                                        onClick = { showDeleteDialog = true },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = stringResource(R.string.delete_forever),
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }

                                }

                            }
                        }
                        if (photos.size > 1) {
                            Row(
                                modifier = Modifier
                                    .offset(y = (-24).dp)
                                    .padding(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                photos.forEachIndexed { index, url ->
                                    AsyncImage(
                                        model = url,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(RoundedCornerShape(18.dp))
                                            .clickable { selectedPhotoIndex = index }
                                    )
                                }

                            }

                        }
                        if (showDeleteDialog) {
                            AlertDialog(
                                onDismissRequest = { showDeleteDialog = false },
                                title = { Text(stringResource(R.string.delete_memory_title)) },
                                text = { Text(stringResource(R.string.delete_memory_message)) },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            showDeleteDialog = false
                                            viewModel.deleteMemory(memory.id)
                                        }
                                    ) {
                                        Text(
                                            text = stringResource(R.string.delete),
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showDeleteDialog = false }) {
                                        Text(stringResource(R.string.cancel))
                                    }


                                }
                            )

                        }
                    }
                }

            }

        }

    }
}