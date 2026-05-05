package ru.itis.android.travel_memorize_app.core.data.di

import dagger.Binds
import dagger.Module
import ru.itis.android.travel_memorize_app.core.data.repository.FriendsRepositoryImpl
import ru.itis.android.travel_memorize_app.core.domain.repository.friend.FriendsRepository
import javax.inject.Singleton

@Module
interface FriendsRepositoryModule {

    @Binds
    @Singleton
    fun bindFriendsRepository(
        impl: FriendsRepositoryImpl
    ): FriendsRepository
}