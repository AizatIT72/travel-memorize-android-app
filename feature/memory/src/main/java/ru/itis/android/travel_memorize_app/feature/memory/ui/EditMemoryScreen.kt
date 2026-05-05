package ru.itis.android.travel_memorize_app.feature.memory.ui

import android.app.DatePickerDialog
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import kotlinx.coroutines.flow.collectLatest
import ru.itis.android.travel_memorize_app.core.ui.components.BackButton
import ru.itis.android.travel_memorize_app.core.ui.components.CustomButton
import ru.itis.android.travel_memorize_app.core.ui.theme.LocalExtendedColors
import ru.itis.android.travel_memorize_app.feature.memory.viewmodel.EditMemoryEffect
import ru.itis.android.travel_memorize_app.feature.memory.viewmodel.EditMemoryViewModel
import ru.itis.android.travel_memorize_app.ui.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val MAX_PHOTOS = 10

@Composable
fun EditMemoryScreen(
    viewModelFactory: ViewModelProvider.Factory,
    memoryId: String,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    onDeleted: () -> Unit
) {
    val viewModel: EditMemoryViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.state.collectAsState()
    val colors = LocalExtendedColors.current
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var errorResId by remember { mutableStateOf<Int?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val locale: Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        configuration.locales[0]
    } else {
        @Suppress("DEPRECATION")
        configuration.locale
    }
    val dateFormatter = remember(locale) {
        SimpleDateFormat("d MMM yyyy", locale)
    }
    val selectedCalendar = remember(state.visitDate) {
        Calendar.getInstance().apply {
            timeInMillis = state.visitDate
        }
    }
    val dateText = remember(state.visitDate, locale) {
        dateFormatter.format(state.visitDate)
    }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = MAX_PHOTOS)
    ) { uris ->
        uris
            .take(MAX_PHOTOS - state.photos.size)
            .forEach(viewModel::addPhoto)
    }

    LaunchedEffect(memoryId) {
        viewModel.loadMemory(memoryId)
    }
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                EditMemoryEffect.Saved -> onSaved()
                EditMemoryEffect.Deleted -> onDeleted()
                is EditMemoryEffect.ShowError -> errorResId = effect.error.toMemoryMessageRes()
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
    val canSave = state.hasChanges &&
            state.title.isNotBlank() &&
            state.photos.isNotEmpty() &&
            !state.isLoading

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            if (state.isInitialLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    EditMemoryTopBar(onBack = onBack)

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp)
                            .padding(top = 16.dp, bottom = 32.dp)
                    ) {
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            state.photos.forEach { url ->
                                EditablePhotoPreview(
                                    url = url,
                                    onRemove = { viewModel.removePhoto(url) }
                                )
                            }

                            AddSmallPhotoCard(
                                enabled = state.photos.size < MAX_PHOTOS && !state.isLoading,
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(
                                            ActivityResultContracts.PickVisualMedia.ImageOnly
                                        )
                                    )
                                }
                            )
                        }
                        Spacer(Modifier.height(28.dp))
                        SectionLabel(text = stringResource(R.string.place_name_label))
                        Spacer(Modifier.height(8.dp))
                        MemoryEditField(
                            value = state.title,
                            onValueChange = viewModel::updateTitle,
                            placeholder = stringResource(R.string.enter_place_name),
                            height = 56.dp
                        )

                        Spacer(Modifier.height(24.dp))

                        SectionLabel(text = stringResource(R.string.visit_date_label))
                        Spacer(Modifier.height(8.dp))
                        MemoryEditField(
                            value = dateText,
                            onValueChange = {},
                            placeholder = stringResource(R.string.enter_visit_date),
                            readOnly = true,
                            height = 56.dp,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = null,
                                    tint = colors.inputMuted,
                                    modifier = Modifier.clickable {
                                        DatePickerDialog(
                                            context,
                                            { _, year, month, day ->
                                                val newDate = Calendar.getInstance().apply {
                                                    set(year, month, day, 0, 0, 0)
                                                    set(Calendar.MILLISECOND, 0)
                                                }
                                                viewModel.updateVisitDate(newDate.timeInMillis)
                                            },
                                            selectedCalendar.get(Calendar.YEAR),
                                            selectedCalendar.get(Calendar.MONTH),
                                            selectedCalendar.get(Calendar.DAY_OF_MONTH)
                                        ).apply {
                                            datePicker.maxDate = System.currentTimeMillis()
                                        }.show()
                                    }
                                )

                            }


                        )

                        Spacer(Modifier.height(24.dp))
                        SectionLabel(text = stringResource(R.string.story_label))
                        Spacer(Modifier.height(8.dp))
                        MemoryEditField(
                            value = state.description,
                            onValueChange = viewModel::updateDescription,
                            placeholder = stringResource(R.string.enter_story),
                            singleLine = false,
                            height = 156.dp
                        )

                        Spacer(Modifier.height(32.dp))
                        CustomButton(
                            text = stringResource(R.string.save_changes),
                            enabled = canSave,
                            onClick = viewModel::save,
                            endIcon = {
                                if (state.isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        )
                        TextButton(
                            onClick = onBack,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(R.string.cancel).uppercase(),
                                style = MaterialTheme.typography.labelMedium,
                                color = colors.linkText
                            )
                        }

                        Spacer(Modifier.height(16.dp))
                        Divider(color = colors.inputContainer)
                        Spacer(Modifier.height(12.dp))

                        TextButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.delete_memory),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
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
                                viewModel.deleteMemory()
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

@Composable
private fun EditMemoryTopBar(
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.96f))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        BackButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterStart)
        )

        Text(
            text = stringResource(R.string.edit_memory_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun EditablePhotoPreview(
    url: String,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 92.dp, height = 92.dp)
            .clip(RoundedCornerShape(4.dp))
    ) {
        AsyncImage(
            model = url,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(22.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.38f),
                    shape = RoundedCornerShape(999.dp)
                )
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

@Composable
private fun AddSmallPhotoCard(
    enabled: Boolean,
    onClick: () -> Unit
) {
    val colors = LocalExtendedColors.current
    Box(
        modifier = Modifier
            .size(width = 92.dp, height = 92.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(colors.inputContainer.copy(alpha = 0.55f))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.add_photo_short),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun MemoryEditField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    trailingIcon: (@Composable (() -> Unit))? = null,
    singleLine: Boolean = true,
    height: Dp = 56.dp
) {
    val colors = LocalExtendedColors.current

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        readOnly = readOnly,
        singleLine = singleLine,
        trailingIcon = trailingIcon,
        placeholder = {
            Text(
                text = placeholder,
                color = colors.inputMuted,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        shape = RoundedCornerShape(4.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            errorBorderColor = Color.Transparent,
            focusedContainerColor = colors.inputContainer.copy(alpha = 0.58f),
            unfocusedContainerColor = colors.inputContainer.copy(alpha = 0.58f),
            errorContainerColor = colors.inputContainer.copy(alpha = 0.58f),
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
        )
    )


}