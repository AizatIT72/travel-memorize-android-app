package ru.itis.android.travel_memorize_app.core.data.di

import dagger.Binds
import dagger.Module
import ru.itis.android.travel_memorize_app.api.BuildConfigProvider
import ru.itis.android.travel_memorize_app.impl.BuildConfigProviderImpl
import javax.inject.Singleton

@Module
interface BuildConfigModule {

    @Binds
    @Singleton
    fun bindBuildConfigProvider(
        impl: BuildConfigProviderImpl
    ): BuildConfigProvider
}