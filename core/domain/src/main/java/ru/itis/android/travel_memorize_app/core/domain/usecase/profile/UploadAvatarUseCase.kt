package ru.itis.android.travel_memorize_app.core.domain.usecase.profile

import android.net.Uri
import ru.itis.android.travel_memorize_app.core.domain.repository.profile.ProfileRepository
import javax.inject.Inject

class UploadAvatarUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(uri: Uri, uid: String) = repository.uploadAvatar(uri, uid)
}