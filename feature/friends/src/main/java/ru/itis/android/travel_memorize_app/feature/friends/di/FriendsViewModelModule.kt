package ru.itis.android.travel_memorize_app.feature.friends.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.itis.android.travel_memorize_app.feature.friends.viewmodel.AddFriendViewModel
import ru.itis.android.travel_memorize_app.feature.friends.viewmodel.FriendProfileViewModel
import ru.itis.android.travel_memorize_app.feature.friends.viewmodel.FriendsViewModel
import ru.itis.android.travel_memorize_app.utils.di.ViewModelKey

@Module
interface FriendsViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(FriendsViewModel::class)
    fun bindFriendsViewModel(
        viewModel: FriendsViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AddFriendViewModel::class)
    fun bindAddFriendViewModel(
        viewModel: AddFriendViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FriendProfileViewModel::class)
    fun bindFriendProfileViewModel(
        viewModel: FriendProfileViewModel
    ): ViewModel
}