package ru.itis.android.travel_memorize_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import ru.itis.android.travel_memorize_app.core.domain.utils.Result
import ru.itis.android.travel_memorize_app.core.ui.theme.TravelMemorizeTheme
import ru.itis.android.travel_memorize_app.navigation.AppNavGraph
import ru.itis.android.travel_memorize_app.navigation.Routes

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appComponent = (application as TravelMemorizeApplication).appComponent
        enableEdgeToEdge()

        setContent {
            var startDestination by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(Unit) {
                val currentUserResult = appComponent.getCurrentUserUseCase().invoke()
                startDestination = when (currentUserResult) {
                    is Result.Success -> {
                        val user = currentUserResult.data
                        if (user != null) Routes.Map else Routes.Onboarding
                    }
                    is Result.Error -> Routes.Onboarding
                }
            }

            TravelMemorizeTheme {
                startDestination?.let {
                    AppNavGraph(
                        startDestination = it,
                        signUpUseCase = appComponent.signUpUseCase(),
                        signInUseCase = appComponent.signInUseCase(),
                        sendPasswordResetUseCase = appComponent.sendPasswordResetUseCase()
                    )
                }
            }
        }
    }
}