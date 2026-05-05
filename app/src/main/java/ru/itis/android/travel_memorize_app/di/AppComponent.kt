package ru.itis.android.travel_memorize_app.di

import androidx.lifecycle.ViewModelProvider
import dagger.Component
import ru.itis.android.travel_memorize_app.core.data.di.BuildConfigModule
import javax.inject.Singleton
import ru.itis.android.travel_memorize_app.core.data.di.DataBindModule
import ru.itis.android.travel_memorize_app.core.data.di.DataModule
import ru.itis.android.travel_memorize_app.core.data.di.FriendsRepositoryModule
import ru.itis.android.travel_memorize_app.core.data.di.MapDataBindModule
import ru.itis.android.travel_memorize_app.core.data.di.MapDataModule
import ru.itis.android.travel_memorize_app.core.data.di.MemoryDataModule
import ru.itis.android.travel_memorize_app.core.data.di.MemoryRepositoryModule
import ru.itis.android.travel_memorize_app.core.data.di.ProfileRepositoryModule
import ru.itis.android.travel_memorize_app.core.domain.usecase.GetCurrentUserUseCase
import ru.itis.android.travel_memorize_app.core.domain.usecase.SignOutUseCase
import ru.itis.android.travel_memorize_app.feature.auth.di.AuthViewModelModule
import ru.itis.android.travel_memorize_app.feature.friends.di.FriendsViewModelModule
import ru.itis.android.travel_memorize_app.feature.memory.di.MemoryViewModelModule
import ru.itis.android.travel_memorize_app.feature.profile.di.ProfileViewModelModule
import ru.itis.android.travel_memorize_app.map.di.MapViewModelModule
import ru.itis.android.travel_memorize_app.network.di.MapNetworkModule

@Singleton
@Component(modules = [
    DataModule::class,
    DataBindModule::class,
    BuildConfigModule::class,
    AuthViewModelModule::class,
    MapDataBindModule::class,
    MapDataModule::class,
    MapViewModelModule::class,
    MemoryRepositoryModule::class,
    MemoryDataModule::class,
    MemoryViewModelModule::class,
    ViewModelFactoryModule::class,
    MapNetworkModule::class,
    BuildConfigModule::class,
    ProfileViewModelModule::class,
    ProfileRepositoryModule::class,
    FriendsRepositoryModule::class,
    FriendsViewModelModule::class
])
interface AppComponent {
    fun viewModelFactory(): ViewModelProvider.Factory
    fun signOutUseCase(): SignOutUseCase
    fun getCurrentUserUseCase(): GetCurrentUserUseCase
}