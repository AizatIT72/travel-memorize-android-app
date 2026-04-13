package ru.itis.android.travel_memorize_app.api

interface BuildConfigProvider {
    fun getApiBaseUrl(): String
    fun getApiKey(): String
}
