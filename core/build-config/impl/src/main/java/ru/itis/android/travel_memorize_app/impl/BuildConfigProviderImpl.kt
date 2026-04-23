package ru.itis.android.travel_memorize_app.core.build_config.impl

import ru.itis.android.travel_memorize_app.api.BuildConfigProvider
import ru.itis.android.travel_memorize_app.core.build_config.impl.BuildConfig

class BuildConfigProviderImpl : BuildConfigProvider {
    override fun getApiBaseUrl(): String {
        return BuildConfig.API_BASE_URL
    }

    override fun getApiKey(): String {
        return BuildConfig.MAPBOX_KEY
    }
}
