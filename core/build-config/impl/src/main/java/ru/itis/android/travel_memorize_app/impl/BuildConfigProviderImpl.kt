package ru.itis.android.travel_memorize_app.impl

import ru.itis.android.travel_memorize_app.api.BuildConfigProvider

class BuildConfigProviderImpl : BuildConfigProvider {
    override fun getApiBaseUrl(): String {
        return BuildConfig.API_BASE_URL
    }

    override fun getApiKey(): String {
        return BuildConfig.MAPBOX_KEY
    }
}
