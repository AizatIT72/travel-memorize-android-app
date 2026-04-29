package ru.itis.android.travel_memorize_app.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
    viewModelFactory: ViewModelProvider.Factory
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
                },
                onNavigateToSignIn = {
                    navController.navigate(Routes.SignIn)
                }
            )
        }

        composable(Routes.SignUp) {
            val viewModel: SignUpViewModel = viewModel(factory = viewModelFactory)
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
            val viewModel: SignInViewModel = viewModel(factory = viewModelFactory)
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
            val viewModel: ForgotPasswordViewModel = viewModel(factory = viewModelFactory)
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