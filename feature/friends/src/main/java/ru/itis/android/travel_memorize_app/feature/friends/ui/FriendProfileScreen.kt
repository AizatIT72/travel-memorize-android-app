package ru.itis.android.travel_memorize_app.feature.friends.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import kotlinx.coroutines.flow.collectLatest
import ru.itis.android.travel_memorize_app.core.domain.model.map.PlaceMark
import ru.itis.android.travel_memorize_app.core.ui.components.BackButton
import ru.itis.android.travel_memorize_app.feature.friends.viewmodel.FriendProfileEffect
import ru.itis.android.travel_memorize_app.feature.friends.viewmodel.FriendProfileViewModel
import ru.itis.android.travel_memorize_app.feature.memory.ui.toMemoryMessageRes
import ru.itis.android.travel_memorize_app.ui.R
import androidx.compose.ui.res.stringResource
import ru.itis.android.travel_memorize_app.core.domain.model.friend.FriendUser
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FriendProfileScreen(
    viewModelFactory: ViewModelProvider.Factory,
    friendId: String,
    onBack: () -> Unit,
    onMemoryClick: (String) -> Unit,
    onShowMapClick: (String) -> Unit
) {
    val viewModel: FriendProfileViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var errorResId by remember { mutableStateOf<Int?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    LaunchedEffect(friendId) {
        viewModel.load(friendId)
    }
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is FriendProfileEffect.ShowError -> errorResId = effect.error.toMemoryMessageRes()
                FriendProfileEffect.RemovedFromFriends -> onBack()
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
        )
        {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.profile == null -> {
                    Text(
                        text = stringResource(R.string.profile_not_found),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    val profile = state.profile!!
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 32.dp)
                    ) {
                        item {
                            FriendProfileHeader(
                                username = profile.username,
                                email = profile.email,
                                avatarUrl = profile.avatarUrl,
                                memoriesCount = profile.memoriesCount,
                                countriesCount = profile.countriesCount,
                                citiesCount = profile.citiesCount,
                                onBack = onBack
                            )
                            Spacer(Modifier.height(24.dp))
                            TextButton(
                                onClick = { showDeleteDialog = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.remove_from_friends),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = { onShowMapClick(profile.uid) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 32.dp)
                                    .height(56.dp),
                                shape = RoundedCornerShape(28.dp)
                            ) {
                                Icon(Icons.Default.Map, contentDescription = null)
                                Spacer(Modifier.width(10.dp))
                                Text(stringResource(R.string.show_friend_map))
                            }

                            Spacer(Modifier.height(32.dp))

                            Text(
                                text = stringResource(R.string.latest_memories),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )

                            Spacer(Modifier.height(16.dp))
                        }

                        items(state.memories.take(10), key = { it.id }) { memory ->
                            FriendMemoryItem(
                                memory = memory,
                                onClick = { onMemoryClick(memory.id) },
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )

                            Spacer(Modifier.height(12.dp))
                        }


                    }
                }
            }
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text(stringResource(R.string.remove_from_friends)) },
                    text = { Text(stringResource(R.string.remove_friend_message)) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDeleteDialog = false
                                viewModel.removeFromFriends()
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
private fun FriendProfileHeader(
    username: String,
    email: String,
    avatarUrl: String?,
    memoriesCount: Int,
    countriesCount: Int,
    citiesCount: Int,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(bottom = 32.dp)
    ) {
        BackButton(
            onClick = onBack,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 72.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(112.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                if (avatarUrl.isNullOrBlank()) {
                    Text(
                        text = username.firstOrNull()?.uppercase() ?: "?",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    AsyncImage(
                        model = avatarUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            Spacer(Modifier.height(18.dp))
            Text(
                text = username,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
            )
            Spacer(Modifier.height(28.dp))
            Row(
                modifier = Modifier.padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(memoriesCount.toString(), stringResource(R.string.memories_count))
                StatCard(countriesCount.toString(), stringResource(R.string.countries))
                StatCard(citiesCount.toString(), stringResource(R.string.cities))
            }
        }
    }
}

@Composable
private fun RowScope.StatCard(
    value: String,
    label: String
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
        )
    }
}

@Composable
private fun FriendMemoryItem(
    memory: PlaceMark,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val date = memory.visitDate ?: memory.createdAt
    val dateText = remember(date) {
        SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(Date(date))
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = memory.photos.firstOrNull(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
        )
        Spacer(Modifier.width(14.dp))

        Column {
            Text(
                text = memory.title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = listOfNotNull(memory.country, memory.city)
                    .joinToString(", ")
                    .ifBlank { memory.placeName.orEmpty() },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = dateText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}