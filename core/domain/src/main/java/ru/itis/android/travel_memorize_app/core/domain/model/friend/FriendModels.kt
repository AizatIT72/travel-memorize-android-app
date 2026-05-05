package ru.itis.android.travel_memorize_app.core.domain.model.friend

data class FriendUser(
    val uid: String,
    val username: String,
    val email: String,
    val avatarUrl: String? = null,
    val description: String? = null,
    val location: String? = null,
    val memoriesCount: Int = 0,
    val countriesCount: Int = 0,
    val citiesCount: Int = 0
)

data class FriendRequest(
    val sender: FriendUser,
    val createdAt: Long
)

data class OutgoingFriendRequest(
    val receiver: FriendUser,
    val createdAt: Long
)
enum class FriendSearchStatus {
    SELF,
    FRIEND,
    REQUEST_SENT,
    REQUEST_RECEIVED,
    CAN_ADD
}

data class FriendSearchResult(
    val user: FriendUser,
    val status: FriendSearchStatus
)