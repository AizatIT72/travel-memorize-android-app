package ru.itis.android.travel_memorize_app.feature.auth.di

import dagger.Module
import dagger.Provides

@Module
object AuthModule {
    @Provides
    fun provideUsernameRegex(): Regex = Regex("^[A-Za-z0-9]{3,20}$")
}
