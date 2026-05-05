package ru.itis.android.travel_memorize_app.feature.friends.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collectLatest
import ru.itis.android.travel_memorize_app.core.domain.model.friend.FriendSearchResult
import ru.itis.android.travel_memorize_app.core.domain.model.friend.FriendSearchStatus
import ru.itis.android.travel_memorize_app.feature.friends.viewmodel.AddFriendEffect
import ru.itis.android.travel_memorize_app.feature.friends.viewmodel.AddFriendViewModel
import ru.itis.android.travel_memorize_app.feature.memory.ui.toMemoryMessageRes
import ru.itis.android.travel_memorize_app.ui.R
import androidx.compose.ui.res.stringResource

@Composable
fun AddFriendScreen(
    viewModelFactory: ViewModelProvider.Factory,
    onBack: () -> Unit
) {
    val viewModel: AddFriendViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var errorResId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is AddFriendEffect.ShowError -> errorResId = effect.error.toMemoryMessageRes()
                AddFriendEffect.RequestSent -> snackbarHostState.showSnackbar("Запрос отправлен")
                AddFriendEffect.RequestCancelled -> snackbarHostState.showSnackbar("Запрос отменён")
                AddFriendEffect.RequestAccepted -> snackbarHostState.showSnackbar("Запрос принят")
                AddFriendEffect.RequestDeclined -> snackbarHostState.showSnackbar("Запрос отклонён")
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
        topBar = {
            AddFriendTopBar(onBack = onBack)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            SearchInput(
                value = state.query,
                onValueChange = viewModel::updateQuery,
                onSearch = viewModel::search
            )
            Spacer(Modifier.height(20.dp))

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            LazyColumn(
                contentPadding = PaddingValues(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.results, key = { it.user.uid }) { result ->
                    SearchResultItem(
                        result = result,
                        onSend = { viewModel.sendRequest(result.user.uid) },
                        onCancel = { viewModel.cancelRequest(result.user.uid) },
                        onAccept = { viewModel.acceptRequest(result.user.uid) },
                        onDecline = { viewModel.declineRequest(result.user.uid) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AddFriendTopBar(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        TextButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Text(stringResource(R.string.clear))
        }
        Text(
            text = stringResource(R.string.add_friend),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        TextButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Text(stringResource(R.string.done))
        }
    }
}

@Composable
private fun SearchInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(12.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            modifier = Modifier.weight(1f),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            decorationBox = { inner ->
                if (value.isBlank()) {
                    Text(
                        text = stringResource(R.string.search_friends),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                inner()
            }
        )

        Text(
            text = stringResource(R.string.search),
            modifier = Modifier.clickable(onClick = onSearch),
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun SearchResultItem(
    result: FriendSearchResult,
    onSend: () -> Unit,
    onCancel: () -> Unit,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = result.user.username.firstOrNull()?.uppercase() ?: "?",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = result.user.username,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = result.user.email,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        when (result.status) {
            FriendSearchStatus.SELF -> {
                Text(
                    text = stringResource(R.string.you),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
            FriendSearchStatus.FRIEND -> {
                Text(
                    text = stringResource(R.string.already_friend),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            FriendSearchStatus.REQUEST_SENT -> {
                TextButton(onClick = onCancel) {
                    Text(stringResource(R.string.sent))
                }
            }
            FriendSearchStatus.REQUEST_RECEIVED -> {
                Row {
                    IconButton(
                        onClick = onAccept,
                        modifier = Modifier
                            .size(38.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                    ) {
                        Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.onPrimary)
                    }
                    Spacer(Modifier.width(6.dp))
                    IconButton(
                        onClick = onDecline,
                        modifier = Modifier
                            .size(38.dp)
                            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.12f), CircleShape)
                    ) {
                        Icon(Icons.Default.Close, null, tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
            FriendSearchStatus.CAN_ADD -> {
                Button(
                    onClick = onSend,
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(stringResource(R.string.add))
                }
            }
        }
    }
}