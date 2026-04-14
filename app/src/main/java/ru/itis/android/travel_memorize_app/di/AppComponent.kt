package ru.itis.android.travel_memorize_app.di

import dagger.Component
import javax.inject.Singleton
import ru.itis.android.travel_memorize_app.core.data.di.DataBindModule
import ru.itis.android.travel_memorize_app.core.data.di.DataModule
import ru.itis.android.travel_memorize_app.core.domain.usecase.GetCurrentUserUseCase
import ru.itis.android.travel_memorize_app.core.domain.usecase.SendPasswordResetUseCase
import ru.itis.android.travel_memorize_app.core.domain.usecase.SignInUseCase
import ru.itis.android.travel_memorize_app.core.domain.usecase.SignOutUseCase
import ru.itis.android.travel_memorize_app.core.domain.usecase.SignUpUseCase
import ru.itis.android.travel_memorize_app.feature.auth.di.AuthModule

@Singleton
@Component(modules = [DataModule::class, DataBindModule::class, AuthModule::class])
interface AppComponent {
    fun signUpUseCase(): SignUpUseCase
    fun signInUseCase(): SignInUseCase
    fun sendPasswordResetUseCase(): SendPasswordResetUseCase
    fun signOutUseCase(): SignOutUseCase
    fun getCurrentUserUseCase(): GetCurrentUserUseCase
}
