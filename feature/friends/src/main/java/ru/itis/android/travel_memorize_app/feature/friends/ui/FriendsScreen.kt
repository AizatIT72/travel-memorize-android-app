package ru.itis.android.travel_memorize_app.feature.friends.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collectLatest
import ru.itis.android.travel_memorize_app.core.domain.model.friend.FriendRequest
import ru.itis.android.travel_memorize_app.core.domain.model.friend.FriendUser
import ru.itis.android.travel_memorize_app.feature.friends.viewmodel.FriendsEffect
import ru.itis.android.travel_memorize_app.feature.friends.viewmodel.FriendsViewModel
import ru.itis.android.travel_memorize_app.feature.memory.ui.toMemoryMessageRes
import ru.itis.android.travel_memorize_app.ui.R
import androidx.compose.ui.res.stringResource

@Composable
fun FriendsScreen(
    viewModelFactory: ViewModelProvider.Factory,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    onAddFriendClick: () -> Unit,
    onFriendClick: (String) -> Unit
) {
    val viewModel: FriendsViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var errorResId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is FriendsEffect.ShowError -> errorResId = effect.error.toMemoryMessageRes()
                else -> Unit
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
                onClick = onAddFriendClick,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null)
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(bottom = paddingValues.calculateBottomPadding())
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 28.dp, bottom = 120.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.friends),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(28.dp))
                Text(
                    text = stringResource(R.string.your_friends),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(16.dp))
                FakeSearchButton(onClick = onAddFriendClick)
                Spacer(Modifier.height(28.dp))
            }

            if (state.incomingRequests.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = stringResource(R.string.friend_requests),
                        count = state.incomingRequests.size
                    )
                    Spacer(Modifier.height(12.dp))
                }
                items(state.incomingRequests, key = { it.sender.uid }) { request ->
                    IncomingRequestItem(
                        request = request,
                        onAccept = { viewModel.acceptRequest(request.sender.uid) },
                        onDecline = { viewModel.declineRequest(request.sender.uid) }
                    )
                    Spacer(Modifier.height(12.dp))
                }
                item {
                    Spacer(Modifier.height(18.dp))
                }
            }
            if (state.outgoingRequests.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = stringResource(R.string.sent_requests),
                        count = state.outgoingRequests.size
                    )
                    Spacer(Modifier.height(12.dp))
                }
                items(state.outgoingRequests, key = { it.receiver.uid }) { request ->
                    FriendUserItem(
                        user = request.receiver,
                        trailing = {
                            TextButton(
                                onClick = {
                                    viewModel.cancelOutgoingRequest(request.receiver.uid)
                                }
                            ) {
                                Text(stringResource(R.string.cancel))
                            }
                        }
                    )
                    Spacer(Modifier.height(12.dp))
                }
                item {
                    Spacer(Modifier.height(18.dp))
                }


            }
            item {
                SectionHeader(
                    title = stringResource(R.string.all_friends),
                    count = state.friends.size
                )
                Spacer(Modifier.height(12.dp))
            }
            if (state.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }

                }
            } else if (state.friends.isEmpty()) {
                item {
                    EmptyFriendsCard()
                }
            } else {
                items(state.friends, key = { it.uid }) { friend ->
                    FriendUserItem(
                        user = friend,
                        modifier = Modifier.clickable { onFriendClick(friend.uid) }
                    )

                    Spacer(Modifier.height(12.dp))
                }

            }
        }
    }
}
@Composable
private fun FakeSearchButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
            modifier = Modifier.size(20.dp)

        )

        Spacer(Modifier.width(12.dp))

        Text(
            text = stringResource(R.string.search_friends),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)

        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    count: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f)
        )
    }
}

@Composable
private fun IncomingRequestItem(
    request: FriendRequest,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    FriendUserItem(
        user = request.sender,
        trailing = {
            Row {
                IconButton(
                    onClick = onAccept,
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = onDecline,
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.error.copy(alpha = 0.12f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                }


            }
        }


    )
}

@Composable
private fun FriendUserItem(
    user: FriendUser,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FriendAvatar(user = user)
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.username,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = user.email,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        trailing?.invoke()
    }
}

@Composable
private fun FriendAvatar(user: FriendUser) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
    ) {
        if (user.avatarUrl.isNullOrBlank()) {
            Text(
                text = user.username.firstOrNull()?.uppercase() ?: "?",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            coil3.compose.AsyncImage(
                model = user.avatarUrl,
                contentDescription = null,
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}


@Composable
private fun EmptyFriendsCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 36.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.PersonAdd,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.65f),
            modifier = Modifier.size(44.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.no_friends_yet),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.no_friends_description),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
        )
    }


}