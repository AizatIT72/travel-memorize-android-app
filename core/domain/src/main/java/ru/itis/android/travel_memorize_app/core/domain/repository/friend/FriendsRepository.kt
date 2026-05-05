package ru.itis.android.travel_memorize_app.core.domain.repository.friend

import kotlinx.coroutines.flow.Flow
import ru.itis.android.travel_memorize_app.core.domain.model.friend.FriendRequest
import ru.itis.android.travel_memorize_app.core.domain.model.friend.FriendSearchResult
import ru.itis.android.travel_memorize_app.core.domain.model.friend.FriendUser
import ru.itis.android.travel_memorize_app.core.domain.model.friend.OutgoingFriendRequest
import ru.itis.android.travel_memorize_app.core.domain.model.map.PlaceMark
import ru.itis.android.travel_memorize_app.core.domain.utils.MapError
import ru.itis.android.travel_memorize_app.core.domain.utils.Result

interface FriendsRepository {

    fun observeFriends(currentUid: String): Flow<List<FriendUser>>
    fun observeIncomingRequests(currentUid: String): Flow<List<FriendRequest>>
    fun observeOutgoingRequests(currentUid: String): Flow<List<OutgoingFriendRequest>>
    suspend fun searchUsers(
        currentUid: String,
        query: String
    ): Result<List<FriendSearchResult>, MapError>

    suspend fun sendFriendRequest(
        currentUid: String,
        receiverId: String
    ): Result<Unit, MapError>

    suspend fun cancelFriendRequest(
        currentUid: String,
        receiverId: String
    ): Result<Unit, MapError>

    suspend fun acceptFriendRequest(
        currentUid: String,
        senderId: String
    ): Result<Unit, MapError>

    suspend fun declineFriendRequest(
        currentUid: String,
        senderId: String
    ): Result<Unit, MapError>

    suspend fun removeFriend(
        currentUid: String,
        friendId: String
    ): Result<Unit, MapError>

    suspend fun getFriendProfile(
        currentUid: String,
        friendId: String
    ): Result<FriendUser, MapError>

    suspend fun getFriendMemories(
        currentUid: String,
        friendId: String
    ): Result<List<PlaceMark>, MapError>

    suspend fun areFriends(
        currentUid: String,
        otherUid: String
    ): Boolean
}