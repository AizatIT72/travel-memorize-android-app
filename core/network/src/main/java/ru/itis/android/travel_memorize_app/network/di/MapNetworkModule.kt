package ru.itis.android.travel_memorize_app.network.di

import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.itis.android.travel_memorize_app.api.BuildConfigProvider
import ru.itis.android.travel_memorize_app.network.api.MapboxApi
import javax.inject.Singleton

@Module
object MapNetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        buildConfigProvider: BuildConfigProvider
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val newUrl = original.url.newBuilder()
                    .addQueryParameter(
                        "access_token",
                        buildConfigProvider.getMapboxAccessToken()
                    )
                    .build()

                chain.proceed(
                    original.newBuilder()
                        .url(newUrl)
                        .build()
                )
            }
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        buildConfigProvider: BuildConfigProvider,
        client: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(buildConfigProvider.getMapboxBaseUrl()) // https://api.mapbox.com/
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideMapboxApi(retrofit: Retrofit): MapboxApi {
        return retrofit.create(MapboxApi::class.java)
    }
}