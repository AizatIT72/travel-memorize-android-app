package ru.itis.android.travel_memorize_app.core.data.di

import dagger.Binds
import dagger.Module
import ru.itis.android.travel_memorize_app.core.data.repository.AuthRepositoryImpl
import ru.itis.android.travel_memorize_app.core.data.repository.MapRepositoryImpl
import ru.itis.android.travel_memorize_app.core.domain.repository.AuthRepository
import ru.itis.android.travel_memorize_app.core.domain.repository.MapRepository
import javax.inject.Singleton

@Module(includes = [FirebaseModule::class])
interface DataModule {
    
    @Binds
    @Singleton
    fun bindMapRepository(impl: MapRepositoryImpl): MapRepository

    @Binds
    @Singleton
    fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}
