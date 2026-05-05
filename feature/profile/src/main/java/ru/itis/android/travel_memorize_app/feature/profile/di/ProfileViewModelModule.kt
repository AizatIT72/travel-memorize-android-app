package ru.itis.android.travel_memorize_app.feature.profile.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.itis.android.travel_memorize_app.utils.di.ViewModelKey
import ru.itis.android.travel_memorize_app.feature.profile.viewmodel.EditProfileViewModel
import ru.itis.android.travel_memorize_app.feature.profile.viewmodel.ProfileViewModel

@Module
interface ProfileViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    fun bindProfileViewModel(viewModel: ProfileViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EditProfileViewModel::class)
    fun bindEditProfileViewModel(viewModel: EditProfileViewModel): ViewModel
}