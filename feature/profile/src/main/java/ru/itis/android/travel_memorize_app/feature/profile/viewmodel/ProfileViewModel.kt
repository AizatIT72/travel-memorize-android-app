package ru.itis.android.travel_memorize_app.feature.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.itis.android.travel_memorize_app.core.domain.model.profile.UserProfile
import ru.itis.android.travel_memorize_app.core.domain.usecase.GetCurrentUserUseCase
import ru.itis.android.travel_memorize_app.core.domain.usecase.SignOutUseCase
import ru.itis.android.travel_memorize_app.core.domain.usecase.profile.GetProfileUseCase
import ru.itis.android.travel_memorize_app.core.domain.utils.MapError
import ru.itis.android.travel_memorize_app.core.domain.utils.Result
import javax.inject.Inject

data class ProfileState(
    val profile: UserProfile? = null,
    val isLoading: Boolean = false
)

sealed interface ProfileEffect {
    data object LoggedOut : ProfileEffect
    data class ShowError(val error: MapError) : ProfileEffect
}

class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ProfileEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val user = (getCurrentUserUseCase() as? Result.Success)?.data
            if (user == null) {
                _state.update { it.copy(isLoading = false) }
                _effect.send(ProfileEffect.ShowError(MapError.Unknown))
                return@launch
            }

            when (val result = getProfileUseCase(user.uid)) {
                is Result.Success -> {
                    _state.update {
                        it.copy(profile = result.data, isLoading = false)
                    }
                }
                is Result.Error -> {
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(ProfileEffect.ShowError(result.error))
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            signOutUseCase()
            _effect.send(ProfileEffect.LoggedOut)
        }
    }
}