package ru.itis.android.travel_memorize_app.impl

import ru.itis.android.travel_memorize_app.api.BuildConfigProvider
import javax.inject.Inject

class BuildConfigProviderImpl @Inject constructor() : BuildConfigProvider {
    override fun getMapboxBaseUrl(): String {
        return BuildConfig.MAPBOX_BASE_URL
    }

    override fun getMapboxAccessToken(): String {
        return BuildConfig.MAPBOX_ACCESS_TOKEN
    }
}