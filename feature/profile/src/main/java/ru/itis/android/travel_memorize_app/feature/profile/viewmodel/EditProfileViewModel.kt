package ru.itis.android.travel_memorize_app.feature.profile.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.itis.android.travel_memorize_app.core.domain.model.profile.UserProfile
import ru.itis.android.travel_memorize_app.core.domain.usecase.GetCurrentUserUseCase
import ru.itis.android.travel_memorize_app.core.domain.usecase.profile.DeleteAccountUseCase
import ru.itis.android.travel_memorize_app.core.domain.usecase.profile.GetProfileUseCase
import ru.itis.android.travel_memorize_app.core.domain.usecase.profile.UpdateProfileUseCase
import ru.itis.android.travel_memorize_app.core.domain.usecase.profile.UploadAvatarUseCase
import ru.itis.android.travel_memorize_app.core.domain.utils.MapError
import ru.itis.android.travel_memorize_app.core.domain.utils.Result
import javax.inject.Inject

data class EditProfileState(
    val original: UserProfile? = null,
    val username: String = "",
    val email: String = "",
    val avatarUrl: String? = null,
    val bio: String = "",
    val city: String = "",
    val country: String = "",
    val isLoading: Boolean = false,
    val isInitialLoading: Boolean = false
) {
    val hasChanges: Boolean
        get() {
            val profile = original ?: return false
            return username.trim() != profile.username ||
                    bio.trim() != profile.bio.orEmpty() ||
                    city.trim() != profile.city.orEmpty() ||
                    country.trim() != profile.country.orEmpty() ||
                    avatarUrl != profile.avatarUrl
        }

    val canSave: Boolean
        get() = username.isNotBlank() && !isLoading && hasChanges
}

sealed interface EditProfileEffect {
    data object Saved : EditProfileEffect
    data object AccountDeleted : EditProfileEffect
    data class ShowError(val error: MapError) : EditProfileEffect
}

class EditProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val uploadAvatarUseCase: UploadAvatarUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EditProfileState())
    val state = _state.asStateFlow()

    private val _effect = Channel<EditProfileEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isInitialLoading = true) }

            val user = (getCurrentUserUseCase() as? Result.Success)?.data
            if (user == null) {
                _state.update { it.copy(isInitialLoading = false) }
                _effect.send(EditProfileEffect.ShowError(MapError.Unknown))
                return@launch
            }

            when (val result = getProfileUseCase(user.uid)) {
                is Result.Success -> {
                    val profile = result.data
                    _state.update {
                        it.copy(
                            original = profile,
                            username = profile.username,
                            email = profile.email,
                            avatarUrl = profile.avatarUrl,
                            bio = profile.bio.orEmpty(),
                            city = profile.city.orEmpty(),
                            country = profile.country.orEmpty(),
                            isInitialLoading = false
                        )
                    }
                }

                is Result.Error -> {
                    _state.update { it.copy(isInitialLoading = false) }
                    _effect.send(EditProfileEffect.ShowError(result.error))
                }
            }
        }
    }

    fun updateUsername(value: String) {
        val filtered = value.filter { it.isLetterOrDigit() || it == '_' }.take(24)
        _state.update { it.copy(username = filtered) }
    }

    fun updateBio(value: String) {
        _state.update { it.copy(bio = value.take(180)) }
    }

    fun updateCity(value: String) {
        _state.update { it.copy(city = value.take(60)) }
    }

    fun updateCountry(value: String) {
        _state.update { it.copy(country = value.take(60)) }
    }

    fun uploadAvatar(uri: Uri) {
        viewModelScope.launch {
            val profile = _state.value.original ?: return@launch
            _state.update { it.copy(isLoading = true) }

            when (val result = uploadAvatarUseCase(uri, profile.uid)) {
                is Result.Success -> {
                    _state.update {
                        it.copy(
                            avatarUrl = result.data,
                            isLoading = false
                        )
                    }
                }

                is Result.Error -> {
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(EditProfileEffect.ShowError(result.error))
                }
            }
        }
    }

    fun save() {
        val state = _state.value
        val original = state.original ?: return
        if (!state.canSave) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val updated = original.copy(
                username = state.username.trim(),
                avatarUrl = state.avatarUrl,
                bio = state.bio.trim(),
                city = state.city.trim().takeIf { it.isNotBlank() },
                country = state.country.trim().takeIf { it.isNotBlank() },
                updatedAt = System.currentTimeMillis()
            )

            when (val result = updateProfileUseCase(updated)) {
                is Result.Success -> {
                    _state.update { it.copy(isLoading = false, original = updated) }
                    _effect.send(EditProfileEffect.Saved)
                }

                is Result.Error -> {
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(EditProfileEffect.ShowError(result.error))
                }
            }
        }
    }

    fun deleteAccount() {
        val uid = _state.value.original?.uid ?: return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            when (val result = deleteAccountUseCase(uid)) {
                is Result.Success -> {
                    _effect.send(EditProfileEffect.AccountDeleted)
                }

                is Result.Error -> {
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(EditProfileEffect.ShowError(result.error))
                }
            }
        }
    }
}