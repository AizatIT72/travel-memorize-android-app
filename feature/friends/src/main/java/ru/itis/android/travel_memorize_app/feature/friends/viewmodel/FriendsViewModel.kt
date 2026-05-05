package ru.itis.android.travel_memorize_app.feature.friends.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.itis.android.travel_memorize_app.core.domain.model.friend.FriendRequest
import ru.itis.android.travel_memorize_app.core.domain.model.friend.FriendUser
import ru.itis.android.travel_memorize_app.core.domain.model.friend.OutgoingFriendRequest
import ru.itis.android.travel_memorize_app.core.domain.usecase.GetCurrentUserUseCase
import ru.itis.android.travel_memorize_app.core.domain.usecase.friend.AcceptFriendRequestUseCase
import ru.itis.android.travel_memorize_app.core.domain.usecase.friend.CancelFriendRequestUseCase
import ru.itis.android.travel_memorize_app.core.domain.usecase.friend.DeclineFriendRequestUseCase
import ru.itis.android.travel_memorize_app.core.domain.usecase.friend.ObserveFriendsUseCase
import ru.itis.android.travel_memorize_app.core.domain.usecase.friend.ObserveIncomingRequestsUseCase
import ru.itis.android.travel_memorize_app.core.domain.usecase.friend.ObserveOutgoingRequestsUseCase
import ru.itis.android.travel_memorize_app.core.domain.utils.MapError
import ru.itis.android.travel_memorize_app.core.domain.utils.Result
import javax.inject.Inject

data class FriendsState(
    val friends: List<FriendUser> = emptyList(),
    val incomingRequests: List<FriendRequest> = emptyList(),
    val outgoingRequests: List<OutgoingFriendRequest> = emptyList(),
    val isLoading: Boolean = false,
    val currentUid: String? = null
)

sealed interface FriendsEffect {
    data class ShowError(val error: MapError) : FriendsEffect
    data object RequestAccepted : FriendsEffect
    data object RequestDeclined : FriendsEffect
    data object RequestCancelled : FriendsEffect
}

class FriendsViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val observeFriendsUseCase: ObserveFriendsUseCase,
    private val observeIncomingRequestsUseCase: ObserveIncomingRequestsUseCase,
    private val observeOutgoingRequestsUseCase: ObserveOutgoingRequestsUseCase,
    private val acceptFriendRequestUseCase: AcceptFriendRequestUseCase,
    private val declineFriendRequestUseCase: DeclineFriendRequestUseCase,
    private val cancelFriendRequestUseCase: CancelFriendRequestUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(FriendsState(isLoading = true))
    val state: StateFlow<FriendsState> = _state.asStateFlow()
    private val _effect = Channel<FriendsEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()
    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val user = (getCurrentUserUseCase() as? Result.Success)?.data
            if (user == null) {
                _state.update { it.copy(isLoading = false) }
                _effect.send(FriendsEffect.ShowError(MapError.Unknown))
                return@launch
            }
            _state.update {
                it.copy(currentUid = user.uid)
            }
            observeFriends(user.uid)
            observeIncomingRequests(user.uid)
            observeOutgoingRequests(user.uid)
        }

    }



    private fun observeFriends(currentUid: String) {
        viewModelScope.launch {
            observeFriendsUseCase(currentUid).collect { friends ->
                _state.update {
                    it.copy(friends = friends, isLoading = false)
                }
            }
        }
    }

    private fun observeIncomingRequests(currentUid: String) {
        viewModelScope.launch {
            observeIncomingRequestsUseCase(currentUid).collect { requests ->
                _state.update {
                    it.copy(incomingRequests = requests)
                }
            }
        }
    }

    private fun observeOutgoingRequests(currentUid: String) {
        viewModelScope.launch {
            observeOutgoingRequestsUseCase(currentUid).collect { requests ->
                _state.update {
                    it.copy(outgoingRequests = requests)
                }
            }
        }
    }

    fun acceptRequest(senderId: String) {
        val currentUid = _state.value.currentUid ?: return
        viewModelScope.launch {
            when (val result = acceptFriendRequestUseCase(currentUid, senderId)) {
                is Result.Success -> _effect.send(FriendsEffect.RequestAccepted)
                is Result.Error -> _effect.send(FriendsEffect.ShowError(result.error))
            }
        }
    }

    fun declineRequest(senderId: String) {
        val currentUid = _state.value.currentUid ?: return
        viewModelScope.launch {
            when (val result = declineFriendRequestUseCase(currentUid, senderId)) {
                is Result.Success -> _effect.send(FriendsEffect.RequestDeclined)
                is Result.Error -> _effect.send(FriendsEffect.ShowError(result.error))
            }

        }
    }

    fun cancelOutgoingRequest(receiverId: String) {
        val currentUid = _state.value.currentUid ?: return
        viewModelScope.launch {
            when (val result = cancelFriendRequestUseCase(currentUid, receiverId)) {
                is Result.Success -> _effect.send(FriendsEffect.RequestCancelled)
                is Result.Error -> _effect.send(FriendsEffect.ShowError(result.error))
            }
        }

    }
}