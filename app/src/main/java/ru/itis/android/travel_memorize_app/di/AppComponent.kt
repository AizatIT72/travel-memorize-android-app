package ru.itis.android.travel_memorize_app.di

import dagger.Component
import ru.itis.android.travel_memorize_app.MapActivity
import ru.itis.android.travel_memorize_app.core.data.di.DataModule
import ru.itis.android.travel_memorize_app.core.network.di.NetworkModule
import ru.itis.android.travel_memorize_app.feature.map.di.MapModule
import ru.itis.android.travel_memorize_app.core.build_config.impl.di.BuildConfigModule
import ru.itis.android.travel_memorize_app.core.domain.usecase.GetCurrentUserUseCase
import ru.itis.android.travel_memorize_app.core.domain.usecase.SendPasswordResetUseCase
import ru.itis.android.travel_memorize_app.core.domain.usecase.SignInUseCase
import ru.itis.android.travel_memorize_app.core.domain.usecase.SignUpUseCase
import ru.itis.android.travel_memorize_app.feature.map.presentation.MapViewModel
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        DataModule::class,
        NetworkModule::class,
        BuildConfigModule::class,
        MapModule::class
    ]
)
interface AppComponent {
    fun inject(activity: MapActivity)

    fun signUpUseCase(): SignUpUseCase
    fun signInUseCase(): SignInUseCase
    fun sendPasswordResetUseCase(): SendPasswordResetUseCase
    fun getCurrentUserUseCase(): GetCurrentUserUseCase
    fun mapViewModelFactory(): MapViewModel.Factory
}
