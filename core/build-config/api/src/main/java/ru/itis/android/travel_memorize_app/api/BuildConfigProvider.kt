package ru.itis.android.travel_memorize_app.api

interface BuildConfigProvider {
    fun getMapboxBaseUrl(): String
    fun getMapboxAccessToken(): String
}
