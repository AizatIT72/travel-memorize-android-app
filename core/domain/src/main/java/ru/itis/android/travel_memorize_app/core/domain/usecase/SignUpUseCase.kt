package ru.itis.android.travel_memorize_app.core.domain.usecase

import javax.inject.Inject
import ru.itis.android.travel_memorize_app.core.domain.repository.AuthRepository

class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String, username: String) =
        authRepository.signUp(email = email, password = password, username = username)
}
