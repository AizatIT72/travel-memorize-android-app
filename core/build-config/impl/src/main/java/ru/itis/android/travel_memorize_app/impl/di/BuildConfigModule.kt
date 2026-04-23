package ru.itis.android.travel_memorize_app.core.build_config.impl.di

import dagger.Module
import dagger.Provides
import ru.itis.android.travel_memorize_app.api.BuildConfigProvider
import ru.itis.android.travel_memorize_app.core.build_config.impl.BuildConfigProviderImpl
import javax.inject.Singleton

@Module
object BuildConfigModule {
    @Provides
    @Singleton
    fun provideBuildConfigProvider(): BuildConfigProvider = BuildConfigProviderImpl()
}
