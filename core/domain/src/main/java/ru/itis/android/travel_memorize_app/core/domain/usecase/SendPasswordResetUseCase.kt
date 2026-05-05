package ru.itis.android.travel_memorize_app.core.domain.usecase

import javax.inject.Inject
import ru.itis.android.travel_memorize_app.core.domain.repository.AuthRepository
import ru.itis.android.travel_memorize_app.core.domain.utils.AuthError
import ru.itis.android.travel_memorize_app.core.domain.utils.Result

class SendPasswordResetUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Unit, AuthError> {
        return authRepository.sendPasswordResetEmail(email)
    }
}