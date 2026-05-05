package ru.itis.android.travel_memorize_app.core.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.itis.android.travel_memorize_app.core.domain.model.friend.FriendRequest
import ru.itis.android.travel_memorize_app.core.domain.model.friend.FriendSearchResult
import ru.itis.android.travel_memorize_app.core.domain.model.friend.FriendSearchStatus
import ru.itis.android.travel_memorize_app.core.domain.model.friend.FriendUser
import ru.itis.android.travel_memorize_app.core.domain.model.friend.OutgoingFriendRequest
import ru.itis.android.travel_memorize_app.core.domain.model.map.GeoPoint
import ru.itis.android.travel_memorize_app.core.domain.model.map.PlaceMark
import ru.itis.android.travel_memorize_app.core.domain.repository.friend.FriendsRepository
import ru.itis.android.travel_memorize_app.core.domain.utils.MapError
import ru.itis.android.travel_memorize_app.core.domain.utils.Result
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.emptyList

@Singleton
class FriendsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FriendsRepository {

    override fun observeFriends(currentUid: String): Flow<List<FriendUser>> = callbackFlow {
        val listener = firestore.collection(FRIENDS)
            .document(currentUid)
            .collection(FOLLOWING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val friendIds = snapshot.documents.map { it.id }

                launch {
                    val friends = friendIds
                        .mapNotNull { uid -> loadFriendUser(uid) }
                        .sortedBy { it.username.lowercase() }

                    trySend(friends)
                }
            }

        awaitClose { listener.remove() }
    }

    override fun observeIncomingRequests(currentUid: String): Flow<List<FriendRequest>> = callbackFlow {
        val listener = firestore.collection(FRIENDS)
            .document(currentUid)
            .collection(REQUESTS)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                launch {
                    val requests = snapshot.documents.mapNotNull { doc ->
                        val senderId = doc.getString(SENDER_ID) ?: doc.id
                        val sender = loadFriendUser(senderId) ?: return@mapNotNull null

                        FriendRequest(
                            sender = sender,
                            createdAt = doc.getLongCompat(CREATED_AT) ?: 0L
                        )
                    }.sortedByDescending { it.createdAt }

                    trySend(requests)
                }
            }

        awaitClose { listener.remove() }
    }

    override fun observeOutgoingRequests(currentUid: String): Flow<List<OutgoingFriendRequest>> = callbackFlow {
        val listener = firestore.collectionGroup(REQUESTS)
            .whereEqualTo(SENDER_ID, currentUid)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                launch {
                    val requests = snapshot.documents.mapNotNull { doc ->
                        val receiverId = doc.getString(RECEIVER_ID)
                            ?: doc.reference.parent.parent?.id
                            ?: return@mapNotNull null

                        val receiver = loadFriendUser(receiverId) ?: return@mapNotNull null

                        OutgoingFriendRequest(
                            receiver = receiver,
                            createdAt = doc.getLongCompat(CREATED_AT) ?: 0L
                        )
                    }.sortedByDescending { it.createdAt }

                    trySend(requests)
                }
            }

        awaitClose { listener.remove() }
    }

    override suspend fun searchUsers(
        currentUid: String,
        query: String
    ): Result<List<FriendSearchResult>, MapError> = try {
        val normalized = query.trim().lowercase()

        if (normalized.isBlank()) {
            return Result.Success(emptyList())
        }

        val snapshot = firestore.collection(USERS)
            .whereGreaterThanOrEqualTo(SEARCH_USERNAME, normalized)
            .whereLessThanOrEqualTo(SEARCH_USERNAME, normalized + "\uf8ff")
            .limit(20)
            .get()
            .await()

        val results = snapshot.documents.mapNotNull { doc ->
            val user = doc.toFriendUserWithStats() ?: return@mapNotNull null
            val status = getSearchStatus(currentUid, user.uid)

            FriendSearchResult(
                user = user,
                status = status
            )
        }

        Result.Success(results)
    } catch (_: Throwable) {
        Result.Error(MapError.Network)
    }

    override suspend fun sendFriendRequest(
        currentUid: String,
        receiverId: String
    ): Result<Unit, MapError> = try {
        if (currentUid == receiverId) {
            return Result.Error(MapError.Unknown)
        }

        if (areFriends(currentUid, receiverId)) {
            return Result.Success(Unit)
        }

        val requestRef = firestore.collection(FRIENDS)
            .document(receiverId)
            .collection(REQUESTS)
            .document(currentUid)

        requestRef.set(
            mapOf(
                SENDER_ID to currentUid,
                RECEIVER_ID to receiverId,
                CREATED_AT to Timestamp.now()
            )
        ).await()

        Result.Success(Unit)
    } catch (_: Throwable) {
        Result.Error(MapError.Network)
    }

    override suspend fun cancelFriendRequest(
        currentUid: String,
        receiverId: String
    ): Result<Unit, MapError> = try {
        firestore.collection(FRIENDS)
            .document(receiverId)
            .collection(REQUESTS)
            .document(currentUid)
            .delete()
            .await()

        Result.Success(Unit)
    } catch (_: Throwable) {
        Result.Error(MapError.Network)
    }

    override suspend fun declineFriendRequest(
        currentUid: String,
        senderId: String
    ): Result<Unit, MapError> = try {
        firestore.collection(FRIENDS)
            .document(currentUid)
            .collection(REQUESTS)
            .document(senderId)
            .delete()
            .await()

        Result.Success(Unit)
    } catch (_: Throwable) {
        Result.Error(MapError.Network)
    }

    override suspend fun acceptFriendRequest(
        currentUid: String,
        senderId: String
    ): Result<Unit, MapError> = try {
        val batch = firestore.batch()
        val now = mapOf(ADDED_AT to Timestamp.now())

        val currentFollowing = firestore.collection(FRIENDS)
            .document(currentUid)
            .collection(FOLLOWING)
            .document(senderId)

        val currentFollowers = firestore.collection(FRIENDS)
            .document(currentUid)
            .collection(FOLLOWERS)
            .document(senderId)

        val senderFollowing = firestore.collection(FRIENDS)
            .document(senderId)
            .collection(FOLLOWING)
            .document(currentUid)

        val senderFollowers = firestore.collection(FRIENDS)
            .document(senderId)
            .collection(FOLLOWERS)
            .document(currentUid)

        val request = firestore.collection(FRIENDS)
            .document(currentUid)
            .collection(REQUESTS)
            .document(senderId)

        batch.set(currentFollowing, now)
        batch.set(currentFollowers, now)
        batch.set(senderFollowing, now)
        batch.set(senderFollowers, now)
        batch.delete(request)

        batch.commit().await()

        Result.Success(Unit)
    } catch (_: Throwable) {
        Result.Error(MapError.Network)
    }

    override suspend fun removeFriend(
        currentUid: String,
        friendId: String
    ): Result<Unit, MapError> = try {
        val batch = firestore.batch()

        batch.delete(
            firestore.collection(FRIENDS)
                .document(currentUid)
                .collection(FOLLOWING)
                .document(friendId)
        )

        batch.delete(
            firestore.collection(FRIENDS)
                .document(currentUid)
                .collection(FOLLOWERS)
                .document(friendId)
        )

        batch.delete(
            firestore.collection(FRIENDS)
                .document(friendId)
                .collection(FOLLOWING)
                .document(currentUid)
        )

        batch.delete(
            firestore.collection(FRIENDS)
                .document(friendId)
                .collection(FOLLOWERS)
                .document(currentUid)
        )

        batch.commit().await()

        Result.Success(Unit)
    } catch (_: Throwable) {
        Result.Error(MapError.Network)
    }

    override suspend fun getFriendProfile(
        currentUid: String,
        friendId: String
    ): Result<FriendUser, MapError> = try {
        if (!areFriends(currentUid, friendId)) {
            return Result.Error(MapError.Unknown)
        }

        val user = loadFriendUser(friendId)
            ?: return Result.Error(MapError.PlaceNotFound)

        Result.Success(user)
    } catch (_: Throwable) {
        Result.Error(MapError.Network)
    }

    override suspend fun getFriendMemories(
        currentUid: String,
        friendId: String
    ): Result<List<PlaceMark>, MapError> = try {
        if (!areFriends(currentUid, friendId)) {
            return Result.Error(MapError.Unknown)
        }

        val snapshot = firestore.collection(MEMORIES)
            .whereEqualTo(USER_ID, friendId)
            .get()
            .await()

        val memories = snapshot.documents
            .mapNotNull { it.toPlaceMark() }
            .sortedByDescending { it.visitDate ?: it.createdAt }

        Result.Success(memories)
    } catch (_: Throwable) {
        Result.Error(MapError.Network)
    }

    override suspend fun areFriends(
        currentUid: String,
        otherUid: String
    ): Boolean {
        if (currentUid.isBlank() || otherUid.isBlank()) return false

        val currentFollowing = firestore.collection(FRIENDS)
            .document(currentUid)
            .collection(FOLLOWING)
            .document(otherUid)
            .get()
            .await()
            .exists()

        val otherFollowing = firestore.collection(FRIENDS)
            .document(otherUid)
            .collection(FOLLOWING)
            .document(currentUid)
            .get()
            .await()
            .exists()

        return currentFollowing && otherFollowing
    }

    private suspend fun getSearchStatus(
        currentUid: String,
        otherUid: String
    ): FriendSearchStatus {
        if (currentUid == otherUid) return FriendSearchStatus.SELF

        if (areFriends(currentUid, otherUid)) {
            return FriendSearchStatus.FRIEND
        }

        val sent = firestore.collection(FRIENDS)
            .document(otherUid)
            .collection(REQUESTS)
            .document(currentUid)
            .get()
            .await()
            .exists()

        if (sent) return FriendSearchStatus.REQUEST_SENT

        val received = firestore.collection(FRIENDS)
            .document(currentUid)
            .collection(REQUESTS)
            .document(otherUid)
            .get()
            .await()
            .exists()

        if (received) return FriendSearchStatus.REQUEST_RECEIVED

        return FriendSearchStatus.CAN_ADD
    }

    private suspend fun loadFriendUser(uid: String): FriendUser? {
        val doc = firestore.collection(USERS)
            .document(uid)
            .get()
            .await()

        return doc.toFriendUserWithStats()
    }

    private suspend fun DocumentSnapshot.toFriendUserWithStats(): FriendUser? {
        if (!exists()) return null

        val memoriesSnapshot = firestore.collection(MEMORIES)
            .whereEqualTo(USER_ID, id)
            .get()
            .await()

        val memories = memoriesSnapshot.documents
        val cities = memories.mapNotNull { it.getString(CITY) }.toSet()
        val countries = memories.mapNotNull { it.getString(COUNTRY) }.toSet()

        val stats = get(STATS) as? Map<*, *>

        return FriendUser(
            uid = id,
            username = getString(USERNAME).orEmpty(),
            email = getString(EMAIL).orEmpty(),
            avatarUrl = getString(AVATAR_URL),
            description = getString(DESCRIPTION) ?: getString(BIO),
            location = getString(LOCATION)
                ?: listOfNotNull(getString(CITY), getString(COUNTRY))
                    .joinToString(", ")
                    .ifBlank { null },
            memoriesCount = memories.size.takeIf { it > 0 }
                ?: stats.getIntCompat(MEMORIES_COUNT),
            countriesCount = countries.size.takeIf { it > 0 }
                ?: stats.getIntCompat(COUNTRIES),
            citiesCount = cities.size.takeIf { it > 0 }
                ?: stats.getIntCompat(CITIES)
        )
    }

    private fun DocumentSnapshot.toPlaceMark(): PlaceMark? {
        if (!exists()) return null

        val coordinates = get(COORDINATES) as? Map<*, *>

        val latitude = coordinates?.get(LATITUDE).toDoubleCompat()
            ?: get(LATITUDE).toDoubleCompat()
            ?: getDouble(LATITUDE)

        val longitude = coordinates?.get(LONGITUDE).toDoubleCompat()
            ?: get(LONGITUDE).toDoubleCompat()
            ?: getDouble(LONGITUDE)

        if (latitude == null || longitude == null) return null

        val photos = getStringListCompat(PHOTOS)
            ?: getStringListCompat(PHOTO_URLS)
            ?: getStringListCompat(PHOTO_URLS_IOS)
            ?: emptyList()

        return PlaceMark(
            id = id,
            coordinates = GeoPoint(latitude, longitude),
            title = getString(TITLE).orEmpty(),
            description = getString(DESCRIPTION) ?: getString(STORY),
            photos = photos,
            createdAt = getLongCompat(CREATED_AT) ?: System.currentTimeMillis(),
            updatedAt = getLongCompat(UPDATED_AT),
            placeName = getString(PLACE_NAME),
            visitDate = getLongCompat(VISIT_DATE),
            city = getString(CITY),
            country = getString(COUNTRY),
            userId = getString(USER_ID)
        )
    }
    private fun DocumentSnapshot.getStringListCompat(field: String): List<String>? {
        return when (val value = get(field)) {
            is List<*> -> value.mapNotNull { it as? String }
            else -> null
        }
    }

    private fun DocumentSnapshot.getLongCompat(field: String): Long? {
        return when (val value = get(field)) {
            is Long -> value
            is Int -> value.toLong()
            is Double -> value.toLong()
            is Timestamp -> value.toDate().time
            else -> null
        }
    }

    private fun Any?.toDoubleCompat(): Double? {
        return when (this) {
            is Double -> this
            is Float -> this.toDouble()
            is Long -> this.toDouble()
            is Int -> this.toDouble()
            else -> null
        }
    }

    private fun Map<*, *>?.getIntCompat(key: String): Int {
        val value = this?.get(key)
        return when (value) {
            is Long -> value.toInt()
            is Int -> value
            is Double -> value.toInt()
            else -> 0
        }
    }

    private companion object {
        const val STORY = "story"
        const val USERS = "users"
        const val FRIENDS = "friends"
        const val REQUESTS = "requests"
        const val FOLLOWING = "following"
        const val FOLLOWERS = "followers"
        const val MEMORIES = "memories"

        const val USERNAME = "username"
        const val EMAIL = "email"
        const val AVATAR_URL = "avatarUrl"
        const val DESCRIPTION = "description"
        const val BIO = "bio"
        const val LOCATION = "location"
        const val CITY = "city"
        const val COUNTRY = "country"
        const val STATS = "stats"

        const val MEMORIES_COUNT = "memoriesCount"
        const val COUNTRIES = "countries"
        const val CITIES = "cities"

        const val SENDER_ID = "senderId"
        const val RECEIVER_ID = "receiverId"
        const val CREATED_AT = "createdAt"
        const val UPDATED_AT = "updatedAt"
        const val ADDED_AT = "addedAt"
        const val SEARCH_USERNAME = "searchUsername"

        const val USER_ID = "userId"
        const val TITLE = "title"
        const val PLACE_NAME = "placeName"
        const val VISIT_DATE = "visitDate"
        const val COORDINATES = "coordinates"
        const val LATITUDE = "latitude"
        const val LONGITUDE = "longitude"

        const val PHOTOS = "photos"
        const val PHOTO_URLS = "photoUrls"
        const val PHOTO_URLS_IOS = "photoURLs"
    }
}