package ru.itis.android.travel_memorize_app.core.domain.usecase.friend

import ru.itis.android.travel_memorize_app.core.domain.repository.friend.FriendsRepository
import javax.inject.Inject

class ObserveFriendsUseCase @Inject constructor(
    private val repository: FriendsRepository
) {
    operator fun invoke(currentUid: String) =
        repository.observeFriends(currentUid)
}