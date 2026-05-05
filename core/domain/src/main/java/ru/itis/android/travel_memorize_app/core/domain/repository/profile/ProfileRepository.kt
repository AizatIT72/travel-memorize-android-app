package ru.itis.android.travel_memorize_app.core.domain.repository.profile

import android.net.Uri
import ru.itis.android.travel_memorize_app.core.domain.model.profile.UserProfile
import ru.itis.android.travel_memorize_app.core.domain.utils.MapError
import ru.itis.android.travel_memorize_app.core.domain.utils.Result

interface ProfileRepository {

    suspend fun getProfile(uid: String): Result<UserProfile, MapError>

    suspend fun updateProfile(profile: UserProfile): Result<Unit, MapError>

    suspend fun uploadAvatar(uri: Uri, uid: String): Result<String, MapError>

    suspend fun deleteAccount(uid: String): Result<Unit, MapError>
}