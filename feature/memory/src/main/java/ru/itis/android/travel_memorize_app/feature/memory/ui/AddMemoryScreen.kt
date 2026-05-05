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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collectLatest
import ru.itis.android.travel_memorize_app.core.domain.model.map.GeoPoint
import ru.itis.android.travel_memorize_app.core.ui.components.BackButton
import ru.itis.android.travel_memorize_app.core.ui.components.CustomButton
import ru.itis.android.travel_memorize_app.core.ui.components.CustomTextField
import ru.itis.android.travel_memorize_app.core.ui.theme.LocalExtendedColors
import ru.itis.android.travel_memorize_app.feature.memory.viewmodel.AddMemoryEffect
import ru.itis.android.travel_memorize_app.feature.memory.viewmodel.AddMemoryViewModel
import ru.itis.android.travel_memorize_app.ui.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import coil3.compose.AsyncImage

private const val MAX_PHOTOS = 10

@Composable
fun AddMemoryScreen(
    viewModelFactory: ViewModelProvider.Factory,
    coordinates: GeoPoint,
    placeName: String? = null,
    city: String? = null,
    country: String? = null,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val viewModel: AddMemoryViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.state.collectAsState()
    val colors = LocalExtendedColors.current
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var errorResId by remember { mutableStateOf<Int?>(null) }
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
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is AddMemoryEffect.Finish -> onSaved()
                is AddMemoryEffect.ShowError -> errorResId = effect.error.toMemoryMessageRes()
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
    val canSave = state.title.isNotBlank() &&
            state.photos.isNotEmpty() &&
            !state.isLoading
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.96f))
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally


            ) {
                CustomButton(
                    text = stringResource(R.string.save_memory),
                    enabled = canSave,
                    onClick = {
                        viewModel.saveMemory(
                            coordinates = coordinates,
                            placeName = placeName,
                            city = city,
                            country = country
                        )

                    },
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

                TextButton(onClick = onBack) {
                    Text(
                        text = stringResource(R.string.cancel).uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.linkText
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                AddMemoryTopBar(onBack = onBack)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp)
                        .padding(top = 16.dp, bottom = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SectionLabel(text = stringResource(R.string.photo_section))
                        Text(
                            text = stringResource(
                                R.string.photo_count,
                                state.photos.size,
                                MAX_PHOTOS
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.secondaryText
                        )
                    }

                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AddPhotoCard(
                            enabled = state.photos.size < MAX_PHOTOS && !state.isLoading,
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            }
                        )
                        state.photos.forEach { url ->
                            PhotoPreview(
                                url = url,
                                onRemove = { viewModel.removePhoto(url) }
                            )
                        }
                    }

                    Spacer(Modifier.height(32.dp))
                    SectionLabel(text = stringResource(R.string.place_name_label))
                    Spacer(Modifier.height(8.dp))
                    CustomTextField(
                        value = state.title,
                        onValueChange = viewModel::updateTitle,
                        placeholder = stringResource(R.string.enter_place_name),
                        isError = false
                    )

                    Spacer(Modifier.height(28.dp))
                    SectionLabel(text = stringResource(R.string.visit_date_label))
                    Spacer(Modifier.height(8.dp))
                    CustomTextField(
                        value = dateText,
                        onValueChange = {},
                        placeholder = stringResource(R.string.enter_visit_date),
                        isError = false,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
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

                    Spacer(Modifier.height(28.dp))

                    SectionLabel(text = stringResource(R.string.story_label))
                    Spacer(Modifier.height(8.dp))
                    CustomTextField(
                        value = state.description,
                        onValueChange = viewModel::updateDescription,
                        placeholder = stringResource(R.string.enter_story),
                        isError = false,
                        singleLine = false,
                        modifier = Modifier.height(120.dp)
                    )
                }

            }


        }
    }
}

@Composable
private fun AddMemoryTopBar(
    onBack: () -> Unit
) {
    Box(

        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.92f))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        BackButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterStart)
        )

        Text(
            text = stringResource(R.string.new_memory),
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
private fun AddPhotoCard(
    enabled: Boolean,
    onClick: () -> Unit
) {
    val colors = LocalExtendedColors.current
    Box(
        modifier = Modifier
            .size(width = 128.dp, height = 176.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(colors.inputContainer)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.add_photo),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

    }
}

@Composable
private fun PhotoPreview(
    url: String,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 128.dp, height = 176.dp)
            .clip(RoundedCornerShape(32.dp))
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
                .padding(8.dp)
                .size(22.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.35f),
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