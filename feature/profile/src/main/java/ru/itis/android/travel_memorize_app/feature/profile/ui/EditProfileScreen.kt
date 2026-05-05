package ru.itis.android.travel_memorize_app.feature.profile.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
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
import ru.itis.android.travel_memorize_app.feature.profile.viewmodel.EditProfileEffect
import ru.itis.android.travel_memorize_app.feature.profile.viewmodel.EditProfileViewModel
import ru.itis.android.travel_memorize_app.ui.R

@Composable
fun EditProfileScreen(
    viewModelFactory: ViewModelProvider.Factory,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    onAccountDeleted: () -> Unit
) {
    val viewModel: EditProfileViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.state.collectAsState()
    val colors = LocalExtendedColors.current
    val snackbarHostState = remember { SnackbarHostState() }
    var errorResId by remember { mutableStateOf<Int?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val avatarPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let(viewModel::uploadAvatar)
    }
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                EditProfileEffect.Saved -> onSaved()
                EditProfileEffect.AccountDeleted -> onAccountDeleted()
                is EditProfileEffect.ShowError -> errorResId = effect.error.toProfileMessageRes()
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
            if (state.isInitialLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    EditProfileTopBar(onBack = onBack)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp)
                            .padding(top = 20.dp, bottom = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            AsyncImage(
                                model = state.avatarUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(104.dp)
                                    .clip(CircleShape)
                                    .background(colors.inputContainer)
                            )
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                                    .clickable {
                                        avatarPickerLauncher.launch(
                                            PickVisualMediaRequest(
                                                ActivityResultContracts.PickVisualMedia.ImageOnly
                                            )
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        SectionTitle(stringResource(R.string.personal_identity))
                        Spacer(Modifier.height(24.dp))
                        FieldLabel(stringResource(R.string.username))
                        ProfileEditField(
                            value = state.username,
                            onValueChange = viewModel::updateUsername,
                            placeholder = stringResource(R.string.username_placeholder),
                            height = 56.dp
                        )
                        Spacer(Modifier.height(6.dp))

                        Text(
                            text = stringResource(R.string.username_hint),
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.secondaryText,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(22.dp))

                        FieldLabel(stringResource(R.string.email_address))
                        ProfileEditField(
                            value = state.email,
                            onValueChange = {},
                            placeholder = stringResource(R.string.email_address),
                            readOnly = true,
                            height = 56.dp
                        )
                        Spacer(Modifier.height(22.dp))
                        FieldLabel(stringResource(R.string.location))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ProfileEditField(
                                value = state.city,
                                onValueChange = viewModel::updateCity,
                                placeholder = stringResource(R.string.city_placeholder),
                                height = 56.dp,
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Place,
                                        contentDescription = null,
                                        tint = colors.inputMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                            ProfileEditField(
                                value = state.country,
                                onValueChange = viewModel::updateCountry,
                                placeholder = stringResource(R.string.country_placeholder),
                                height = 56.dp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(Modifier.height(22.dp))
                        FieldLabel(stringResource(R.string.bio))
                        ProfileEditField(
                            value = state.bio,
                            onValueChange = viewModel::updateBio,
                            placeholder = stringResource(R.string.bio_placeholder),
                            singleLine = false,
                            height = 120.dp
                        )

                        Text(
                            text = "${state.bio.length} / 180",
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.secondaryText,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp)
                                .wrapContentWidth(Alignment.End)
                        )

                        Spacer(Modifier.height(34.dp))
                        CustomButton(
                            text = stringResource(R.string.save_changes),
                            enabled = state.canSave,
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
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        )
                        Spacer(Modifier.height(22.dp))
                        AccountPrivacyCard(
                            onDeleteClick = { showDeleteDialog = true }
                        )
                    }
                }
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text(stringResource(R.string.delete_account_title)) },
                    text = { Text(stringResource(R.string.delete_account_message)) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDeleteDialog = false
                                viewModel.deleteAccount()
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
private fun EditProfileTopBar(
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
            text = stringResource(R.string.edit_profile),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(8.dp))


}

@Composable
private fun ProfileEditField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    leadingIcon: (@Composable (() -> Unit))? = null,
    singleLine: Boolean = true,
    height: Dp = 56.dp
) {
    val colors = LocalExtendedColors.current
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        readOnly = readOnly,
        singleLine = singleLine,
        leadingIcon = leadingIcon,
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
            focusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
            unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
            errorBorderColor = androidx.compose.ui.graphics.Color.Transparent,
            focusedContainerColor = colors.inputContainer.copy(alpha = 0.58f),
            unfocusedContainerColor = colors.inputContainer.copy(alpha = 0.58f),
            errorContainerColor = colors.inputContainer.copy(alpha = 0.58f),
            cursorColor = MaterialTheme.colorScheme.primary,

            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
private fun AccountPrivacyCard(
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.account_privacy),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.account_privacy_description),
            style = MaterialTheme.typography.bodySmall,
            color = LocalExtendedColors.current.secondaryText
        )

        Spacer(Modifier.height(24.dp))
        TextButton(onClick = onDeleteClick) {
            Icon(
                imageVector = Icons.Default.DeleteForever,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.delete_account),
                color = MaterialTheme.colorScheme.error
            )
        }


    }
}