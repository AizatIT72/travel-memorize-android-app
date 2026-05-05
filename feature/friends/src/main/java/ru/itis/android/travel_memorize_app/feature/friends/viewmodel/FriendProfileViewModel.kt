package ru.itis.android.travel_memorize_app.feature.friends.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.itis.android.travel_memorize_app.core.domain.model.friend.FriendUser
import ru.itis.android.travel_memorize_app.core.domain.model.map.PlaceMark
import ru.itis.android.travel_memorize_app.core.domain.usecase.GetCurrentUserUseCase
import ru.itis.android.travel_memorize_app.core.domain.usecase.friend.GetFriendMemoriesUseCase
import ru.itis.android.travel_memorize_app.core.domain.usecase.friend.GetFriendProfileUseCase
import ru.itis.android.travel_memorize_app.core.domain.usecase.friend.RemoveFriendUseCase
import ru.itis.android.travel_memorize_app.core.domain.utils.MapError
import ru.itis.android.travel_memorize_app.core.domain.utils.Result
import javax.inject.Inject

data class FriendProfileState(
    val profile: FriendUser? = null,
    val memories: List<PlaceMark> = emptyList(),
    val isLoading: Boolean = false,
    val isRemoving: Boolean = false,
    val currentUid: String? = null
)

sealed interface FriendProfileEffect {
    data class ShowError(val error: MapError) : FriendProfileEffect
    data object RemovedFromFriends : FriendProfileEffect
}

class FriendProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getFriendProfileUseCase: GetFriendProfileUseCase,
    private val getFriendMemoriesUseCase: GetFriendMemoriesUseCase,
    private val removeFriendUseCase: RemoveFriendUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FriendProfileState(isLoading = true))
    val state: StateFlow<FriendProfileState> = _state.asStateFlow()

    private val _effect = Channel<FriendProfileEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun load(friendId: String) {
        if (friendId.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val currentUser = (getCurrentUserUseCase() as? Result.Success)?.data
            if (currentUser == null) {
                _state.update { it.copy(isLoading = false) }
                _effect.send(FriendProfileEffect.ShowError(MapError.Unknown))
                return@launch
            }

            _state.update { it.copy(currentUid = currentUser.uid) }
            when (val profileResult = getFriendProfileUseCase(currentUser.uid, friendId)) {
                is Result.Success -> {
                    _state.update {
                        it.copy(
                            profile = profileResult.data,
                            isLoading = false
                        )
                    }
                }

                is Result.Error -> {
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(FriendProfileEffect.ShowError(profileResult.error))
                    return@launch
                }
            }
            when (val memoriesResult = getFriendMemoriesUseCase(currentUser.uid, friendId)) {
                is Result.Success -> {
                    _state.update {
                        it.copy(
                            memories = memoriesResult.data.sortedByDescending { memory ->
                                memory.visitDate ?: memory.createdAt
                            }
                        )
                    }


                }
                is Result.Error -> {
                    _effect.send(FriendProfileEffect.ShowError(memoriesResult.error))
                }
            }
        }
    }

    fun removeFromFriends() {
        val currentUid = _state.value.currentUid ?: return
        val friendId = _state.value.profile?.uid ?: return
        viewModelScope.launch {
            _state.update { it.copy(isRemoving = true) }
            when (val result = removeFriendUseCase(currentUid, friendId)) {
                is Result.Success -> {
                    _state.update { it.copy(isRemoving = false) }
                    _effect.send(FriendProfileEffect.RemovedFromFriends)
                }
                is Result.Error -> {
                    _state.update { it.copy(isRemoving = false) }
                    _effect.send(FriendProfileEffect.ShowError(result.error))
                }
            }


        }
    }
}