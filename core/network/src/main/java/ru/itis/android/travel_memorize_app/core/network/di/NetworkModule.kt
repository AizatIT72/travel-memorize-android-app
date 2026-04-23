package ru.itis.android.travel_memorize_app.core.network.di

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.itis.android.travel_memorize_app.core.network.api.MapboxApi
import javax.inject.Singleton

@Module
object NetworkModule {
    @Provides
    @Singleton
    fun provideMapboxApi(): MapboxApi {
        return Retrofit.Builder()
            .baseUrl("https://api.mapbox.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MapboxApi::class.java)
    }
}
