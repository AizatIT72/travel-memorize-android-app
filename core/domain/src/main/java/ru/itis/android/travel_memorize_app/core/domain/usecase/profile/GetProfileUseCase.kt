package ru.itis.android.travel_memorize_app.core.domain.usecase.profile

import ru.itis.android.travel_memorize_app.core.domain.repository.profile.ProfileRepository
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(uid: String) = repository.getProfile(uid)
}