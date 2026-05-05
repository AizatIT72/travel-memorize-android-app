package ru.itis.android.travel_memorize_app.core.domain.repository.memory

import android.net.Uri
import ru.itis.android.travel_memorize_app.core.domain.model.map.PlaceMark
import ru.itis.android.travel_memorize_app.core.domain.utils.MapError
import ru.itis.android.travel_memorize_app.core.domain.utils.Result

interface MemoryRepository {

    suspend fun uploadPhoto(
        uri: Uri,
        userId: String
    ): Result<String, MapError>

    suspend fun addMemory(
        placeMark: PlaceMark
    ): Result<Unit, MapError>

    suspend fun updateMemory(
        placeMark: PlaceMark
    ): Result<Unit, MapError>

    suspend fun deleteMemory(
        placeMarkId: String
    ): Result<Unit, MapError>

    suspend fun getMemory(
        placeMarkId: String
    ): Result<PlaceMark, MapError>

    suspend fun getAllMemories(
        userId: String
    ): Result<List<PlaceMark>, MapError>
}