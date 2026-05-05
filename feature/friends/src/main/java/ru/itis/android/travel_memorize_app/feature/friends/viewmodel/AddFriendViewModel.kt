package ru.itis.android.travel_memorize_app.feature.friends.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.itis.android.travel_memorize_app.core.domain.model.friend.FriendSearchResult
import ru.itis.android.travel_memorize_app.core.domain.model.friend.FriendSearchStatus
import ru.itis.android.travel_memorize_app.core.domain.usecase.GetCurrentUserUseCase
import ru.itis.android.travel_memorize_app.core.domain.usecase.friend.AcceptFriendRequestUseCase
import ru.itis.android.travel_memorize_app.core.domain.usecase.friend.CancelFriendRequestUseCase
import ru.itis.android.travel_memorize_app.core.domain.usecase.friend.DeclineFriendRequestUseCase
import ru.itis.android.travel_memorize_app.core.domain.usecase.friend.SearchUsersUseCase
import ru.itis.android.travel_memorize_app.core.domain.usecase.friend.SendFriendRequestUseCase
import ru.itis.android.travel_memorize_app.core.domain.utils.MapError
import ru.itis.android.travel_memorize_app.core.domain.utils.Result
import javax.inject.Inject

data class AddFriendState(
    val query: String = "",
    val results: List<FriendSearchResult> = emptyList(),
    val isLoading: Boolean = false,
    val currentUid: String? = null

)

sealed interface AddFriendEffect {


    data class ShowError(val error: MapError) : AddFriendEffect
    data object RequestSent : AddFriendEffect
    data object RequestCancelled : AddFriendEffect
    data object RequestAccepted : AddFriendEffect
    data object RequestDeclined : AddFriendEffect
}

class AddFriendViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val searchUsersUseCase: SearchUsersUseCase,
    private val sendFriendRequestUseCase: SendFriendRequestUseCase,
    private val cancelFriendRequestUseCase: CancelFriendRequestUseCase,
    private val acceptFriendRequestUseCase: AcceptFriendRequestUseCase,
    private val declineFriendRequestUseCase: DeclineFriendRequestUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(AddFriendState())
    val state: StateFlow<AddFriendState> = _state.asStateFlow()
    private val _effect = Channel<AddFriendEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()
    private var searchJob: Job? = null

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            val user = (getCurrentUserUseCase() as? Result.Success)?.data
            _state.update { it.copy(currentUid = user?.uid) }
        }
    }

    fun updateQuery(query: String) {
        _state.update { it.copy(query = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            kotlinx.coroutines.delay(300)
            search()
        }


    }

    fun search() {
        val currentUid = _state.value.currentUid ?: return
        val query = _state.value.query.trim()
        if (query.isBlank()) {
            _state.update { it.copy(results = emptyList(), isLoading = false) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = searchUsersUseCase(currentUid, query)) {
                is Result.Success -> {
                    _state.update {
                        it.copy(
                            results = result.data,
                            isLoading = false
                        )
                    }
                }
                is Result.Error -> {
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(AddFriendEffect.ShowError(result.error))
                }
            }
        }
    }

    fun sendRequest(receiverId: String) {
        val currentUid = _state.value.currentUid ?: return
        viewModelScope.launch {
            when (val result = sendFriendRequestUseCase(currentUid, receiverId)) {
                is Result.Success -> {
                    updateUserStatus(receiverId, FriendSearchStatus.REQUEST_SENT)
                    search()
                    _effect.send(AddFriendEffect.RequestSent)
                }
                is Result.Error -> {
                    _effect.send(AddFriendEffect.ShowError(result.error))
                }
            }
        }
    }
    fun cancelRequest(receiverId: String) {
        val currentUid = _state.value.currentUid ?: return
        viewModelScope.launch {
            when (val result = cancelFriendRequestUseCase(currentUid, receiverId)) {
                is Result.Success -> {
                    search()
                    updateUserStatus(receiverId, FriendSearchStatus.CAN_ADD)
                    _effect.send(AddFriendEffect.RequestCancelled)
                }
                is Result.Error -> _effect.send(AddFriendEffect.ShowError(result.error))
            }
        }
    }

    fun acceptRequest(senderId: String) {
        val currentUid = _state.value.currentUid ?: return
        viewModelScope.launch {
            when (val result = acceptFriendRequestUseCase(currentUid, senderId)) {
                is Result.Success -> {
                    search()
                    updateUserStatus(senderId, FriendSearchStatus.FRIEND)
                    _effect.send(AddFriendEffect.RequestAccepted)
                }
                is Result.Error -> _effect.send(AddFriendEffect.ShowError(result.error))
            }
        }
    }

    fun declineRequest(senderId: String) {
        val currentUid = _state.value.currentUid ?: return
        viewModelScope.launch {
            when (val result = declineFriendRequestUseCase(currentUid, senderId)) {
                is Result.Success -> {
                    search()
                    updateUserStatus(senderId, FriendSearchStatus.CAN_ADD)
                    _effect.send(AddFriendEffect.RequestDeclined)
                }
                is Result.Error -> _effect.send(AddFriendEffect.ShowError(result.error))
            }
        }
    }

    private fun updateUserStatus(
        uid: String,
        status: FriendSearchStatus
    ) {
        _state.update { state ->
            state.copy(
                results = state.results.map { result ->
                    if (result.user.uid == uid) {
                        result.copy(status = status)
                    } else {
                        result
                    }
                }
            )
        }
    }
}