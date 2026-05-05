package ru.itis.android.travel_memorize_app.core.domain.usecase

import javax.inject.Inject
import ru.itis.android.travel_memorize_app.core.domain.repository.AuthRepository
import ru.itis.android.travel_memorize_app.core.domain.utils.AuthError
import ru.itis.android.travel_memorize_app.core.domain.utils.Result

class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit, AuthError> {
        return authRepository.signOut()
    }
}