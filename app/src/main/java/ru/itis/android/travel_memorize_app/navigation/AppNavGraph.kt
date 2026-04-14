package ru.itis.android.travel_memorize_app.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.itis.android.travel_memorize_app.core.domain.usecase.SendPasswordResetUseCase
import ru.itis.android.travel_memorize_app.core.domain.usecase.SignInUseCase
import ru.itis.android.travel_memorize_app.core.domain.usecase.SignUpUseCase
import ru.itis.android.travel_memorize_app.feature.auth.ui.ForgotPasswordScreen
import ru.itis.android.travel_memorize_app.feature.auth.ui.OnboardingScreen
import ru.itis.android.travel_memorize_app.feature.auth.ui.SignInScreen
import ru.itis.android.travel_memorize_app.feature.auth.ui.SignUpScreen
import ru.itis.android.travel_memorize_app.feature.auth.viewmodel.ForgotPasswordViewModel
import ru.itis.android.travel_memorize_app.feature.auth.viewmodel.SignInViewModel
import ru.itis.android.travel_memorize_app.feature.auth.viewmodel.SignUpViewModel
import ru.itis.android.travel_memorize_app.ui.R

object Routes {
    const val Onboarding = "onboarding"
    const val SignUp = "signup"
    const val SignIn = "signin"
    const val ForgotPassword = "forgot_password"
    const val Map = "map"
}

@Composable
fun AppNavGraph(
    startDestination: String,
    signUpUseCase: SignUpUseCase,
    signInUseCase: SignInUseCase,
    sendPasswordResetUseCase: SendPasswordResetUseCase
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.Onboarding) {
            OnboardingScreen(
                onGetStarted = {
                    navController.navigate(Routes.SignUp)
                }
            )
        }

        composable(Routes.SignUp) {
            val viewModel = remember { SignUpViewModel(signUpUseCase) }

            SignUpScreen(
                viewModel = viewModel,
                onBack = {
                    navController.popBackStack()
                },
                onNavigateToSignIn = {
                    navController.navigate(Routes.SignIn) {
                        launchSingleTop = true
                        popUpTo(Routes.SignUp) {
                            inclusive = true
                        }
                    }
                },
                onSuccess = {
                    navController.navigate(Routes.Map) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.SignIn) {
            val viewModel = remember { SignInViewModel(signInUseCase) }

            SignInScreen(
                viewModel = viewModel,
                onBack = {
                    navController.popBackStack()
                },
                onNavigateToSignUp = {
                    navController.navigate(Routes.SignUp) {
                        launchSingleTop = true
                        popUpTo(Routes.SignIn) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Routes.ForgotPassword) {
                        launchSingleTop = true
                    }
                },
                onSuccess = {
                    navController.navigate(Routes.Map) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.ForgotPassword) {
            val viewModel = remember { ForgotPasswordViewModel(sendPasswordResetUseCase) }

            ForgotPasswordScreen(
                viewModel = viewModel,
                onBack = {
                    navController.popBackStack()
                },
                onBackToSignIn = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.Map) {
            Text(text = stringResource(id = R.string.map_placeholder_title))
        }
    }
}