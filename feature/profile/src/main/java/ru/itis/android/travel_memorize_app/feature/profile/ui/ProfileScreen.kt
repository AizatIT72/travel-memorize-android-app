package ru.itis.android.travel_memorize_app.feature.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import kotlinx.coroutines.flow.collectLatest
import ru.itis.android.travel_memorize_app.core.domain.model.profile.UserProfile
import ru.itis.android.travel_memorize_app.core.ui.components.CustomButton
import ru.itis.android.travel_memorize_app.core.ui.theme.AppLanguage
import ru.itis.android.travel_memorize_app.core.ui.theme.LocalExtendedColors
import ru.itis.android.travel_memorize_app.core.ui.theme.ThemeMode
import ru.itis.android.travel_memorize_app.feature.profile.viewmodel.ProfileEffect
import ru.itis.android.travel_memorize_app.feature.profile.viewmodel.ProfileViewModel
import ru.itis.android.travel_memorize_app.ui.R

@Composable
fun ProfileScreen(
    viewModelFactory: ViewModelProvider.Factory,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    appLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onEditProfile: () -> Unit,
    onLogout: () -> Unit
) {
    val viewModel: ProfileViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var errorResId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                ProfileEffect.LoggedOut -> onLogout()
                is ProfileEffect.ShowError -> errorResId = effect.error.toProfileMessageRes()
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
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            when {
                state.isLoading && state.profile == null -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                state.profile == null -> {
                    Text(
                        text = stringResource(R.string.profile_not_found),
                        modifier = Modifier.align(Alignment.Center),
                        color = LocalExtendedColors.current.secondaryText
                    )
                }
                else -> {
                    ProfileContent(
                        profile = state.profile!!,
                        themeMode = themeMode,
                        onThemeModeChange = onThemeModeChange,
                        appLanguage = appLanguage,
                        onLanguageChange = onLanguageChange,
                        onEditProfile = onEditProfile,
                        onLogout = viewModel::logout
                    )
                }


            }
        }


    }
}

@Composable
private fun ProfileContent(
    profile: UserProfile,
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    onEditProfile: () -> Unit,
    onLogout: () -> Unit,
    appLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
) {
    val colors = LocalExtendedColors.current
    var showLanguageMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileTopBar()
        Spacer(Modifier.height(24.dp))
        Box(contentAlignment = Alignment.BottomEnd) {
            AsyncImage(
                model = profile.avatarUrl,
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
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(15.dp)
                )
            }
        }

        Spacer(Modifier.height(18.dp))
        Text(
            text = profile.username,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = profile.email,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.secondaryText
        )
        profile.bio?.takeIf { it.isNotBlank() }?.let {
            Spacer(Modifier.height(12.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.secondaryText
            )
        }

        val location = listOfNotNull(profile.city, profile.country).joinToString(", ")
        if (location.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = colors.secondaryText,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = location,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.secondaryText
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProfileStatCard(
                value = profile.countriesCount.toString(),
                label = stringResource(R.string.profile_countries),
                modifier = Modifier.weight(1f)
            )
            ProfileStatCard(
                value = profile.citiesCount.toString(),
                label = stringResource(R.string.profile_cities),
                modifier = Modifier.weight(1f)
            )
            ProfileStatCard(
                value = profile.memoriesCount.toString(),
                label = stringResource(R.string.profile_memories),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(36.dp))

        SectionTitle(stringResource(R.string.profile_preferences))
        Spacer(Modifier.height(12.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            PreferenceRow(
                icon = Icons.Default.NightsStay,
                title = stringResource(R.string.dark_mode),
                trailing = {
                    Switch(
                        checked = themeMode == ThemeMode.DARK,
                        onCheckedChange = { checked ->
                            onThemeModeChange(
                                if (checked) ThemeMode.DARK else ThemeMode.LIGHT
                            )
                        }
                    )
                }
            )

            Divider(color = MaterialTheme.colorScheme.background)

            Box {
                PreferenceRow(
                    icon = Icons.Default.Language,
                    title = stringResource(R.string.language),
                    trailing = {
                        TextButton(onClick = { showLanguageMenu = true }) {
                            Text(
                                when (appLanguage) {
                                    AppLanguage.RU -> "Русский"
                                    AppLanguage.EN -> "English"
                                }
                            )
                        }
                    }
                )
                DropdownMenu(
                    expanded = showLanguageMenu,
                    onDismissRequest = { showLanguageMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Русский") },
                        onClick = {
                            onLanguageChange(AppLanguage.RU)
                            showLanguageMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("English") },
                        onClick = {
                            onLanguageChange(AppLanguage.EN)
                            showLanguageMenu = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.weight(1f))

        CustomButton(
            text = stringResource(R.string.edit_profile),
            enabled = true,
            onClick = onEditProfile,
            endIcon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        )
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.logout))
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun ProfileTopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.profile),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ProfileStatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .height(72.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = LocalExtendedColors.current.secondaryText
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun PreferenceRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(Modifier.width(14.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        trailing()


    }
}