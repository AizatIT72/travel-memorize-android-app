package ru.itis.android.travel_memorize_app.core.domain.usecase

import javax.inject.Inject
import ru.itis.android.travel_memorize_app.core.domain.repository.AuthRepository

class SignInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String) =
        authRepository.signIn(email = email, password = password)
}
