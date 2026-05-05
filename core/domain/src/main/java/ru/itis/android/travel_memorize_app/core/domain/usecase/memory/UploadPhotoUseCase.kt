package ru.itis.android.travel_memorize_app.core.domain.usecase.memory

import android.net.Uri
import ru.itis.android.travel_memorize_app.core.domain.repository.memory.MemoryRepository
import ru.itis.android.travel_memorize_app.core.domain.utils.MapError
import ru.itis.android.travel_memorize_app.core.domain.utils.Result
import javax.inject.Inject

class UploadPhotoUseCase @Inject constructor(
    private val repository: MemoryRepository
) {
    suspend operator fun invoke(uri: Uri, userId: String): Result<String, MapError> =
        repository.uploadPhoto(uri, userId)
}