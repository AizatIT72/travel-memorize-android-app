package ru.itis.android.travel_memorize_app.core.domain.model.profile

data class UserProfile(
    val uid: String,
    val username: String,
    val email: String,
    val avatarUrl: String? = null,
    val bio: String? = null,
    val city: String? = null,
    val country: String? = null,
    val memoriesCount: Int = 0,
    val citiesCount: Int = 0,
    val countriesCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long? = null
)