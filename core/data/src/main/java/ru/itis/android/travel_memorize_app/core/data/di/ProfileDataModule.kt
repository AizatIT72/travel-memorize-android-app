package ru.itis.android.travel_memorize_app.core.data.di

import dagger.Binds
import dagger.Module
import ru.itis.android.travel_memorize_app.core.data.repository.ProfileRepositoryImpl
import ru.itis.android.travel_memorize_app.core.domain.repository.profile.ProfileRepository
import javax.inject.Singleton

@Module
interface ProfileRepositoryModule {

    @Binds
    @Singleton
    fun bindProfileRepository(
        impl: ProfileRepositoryImpl
    ): ProfileRepository
}