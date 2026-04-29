package ru.itis.android.travel_memorize_app.di

import androidx.lifecycle.ViewModelProvider
import dagger.Component
import javax.inject.Singleton
import ru.itis.android.travel_memorize_app.core.data.di.DataBindModule
import ru.itis.android.travel_memorize_app.core.data.di.DataModule
import ru.itis.android.travel_memorize_app.core.domain.usecase.GetCurrentUserUseCase
import ru.itis.android.travel_memorize_app.core.domain.usecase.SignOutUseCase
import ru.itis.android.travel_memorize_app.feature.auth.di.AuthViewModelModule

@Singleton
@Component(modules = [DataModule::class, DataBindModule::class, ViewModelFactoryModule::class, AuthViewModelModule::class])
interface AppComponent {
    fun viewModelFactory(): ViewModelProvider.Factory
    fun signOutUseCase(): SignOutUseCase
    fun getCurrentUserUseCase(): GetCurrentUserUseCase
}
