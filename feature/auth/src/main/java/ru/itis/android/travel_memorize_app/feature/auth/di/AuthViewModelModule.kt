package ru.itis.android.travel_memorize_app.feature.auth.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.itis.android.travel_memorize_app.feature.auth.viewmodel.ForgotPasswordViewModel
import ru.itis.android.travel_memorize_app.feature.auth.viewmodel.SignInViewModel
import ru.itis.android.travel_memorize_app.feature.auth.viewmodel.SignUpViewModel
import ru.itis.android.travel_memorize_app.utils.di.ViewModelKey

@Module
interface AuthViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(SignUpViewModel::class)
    fun bindSignUpViewModel(viewModel: SignUpViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SignInViewModel::class)
    fun bindSignInViewModel(viewModel: SignInViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ForgotPasswordViewModel::class)
    fun bindForgotPasswordViewModel(viewModel: ForgotPasswordViewModel): ViewModel
}