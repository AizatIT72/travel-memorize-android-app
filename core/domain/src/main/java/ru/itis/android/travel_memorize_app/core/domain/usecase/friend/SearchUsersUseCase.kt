package ru.itis.android.travel_memorize_app.core.domain.usecase.friend

import ru.itis.android.travel_memorize_app.core.domain.repository.friend.FriendsRepository
import javax.inject.Inject

class SearchUsersUseCase @Inject constructor(
    private val repository: FriendsRepository
) {
    suspend operator fun invoke(currentUid: String, query: String) =
        repository.searchUsers(currentUid, query)
}