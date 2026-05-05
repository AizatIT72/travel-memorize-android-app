package ru.itis.android.travel_memorize_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.os.LocaleListCompat
import ru.itis.android.travel_memorize_app.core.domain.utils.Result
import ru.itis.android.travel_memorize_app.core.ui.theme.AppLanguage
import ru.itis.android.travel_memorize_app.core.ui.theme.ThemeMode
import ru.itis.android.travel_memorize_app.core.ui.theme.TravelMemorizeTheme
import ru.itis.android.travel_memorize_app.navigation.AppNavGraph
import ru.itis.android.travel_memorize_app.navigation.Routes

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appComponent = (application as TravelMemorizeApplication).appComponent
        enableEdgeToEdge()

        setContent {
            var startDestination by rememberSaveable { mutableStateOf<String?>(null) }
            var themeMode by rememberSaveable { mutableStateOf(ThemeMode.SYSTEM) }
            var appLanguage by rememberSaveable { mutableStateOf(AppLanguage.RU) }

            LaunchedEffect(appLanguage) {
                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.forLanguageTags(appLanguage.tag)
                )
            }

            LaunchedEffect(Unit) {
                val currentUserResult = appComponent.getCurrentUserUseCase().invoke()
                startDestination = when (currentUserResult) {
                    is Result.Success -> {
                        if (currentUserResult.data != null) Routes.Map else Routes.Onboarding
                    }

                    is Result.Error -> Routes.Onboarding
                }
            }

            TravelMemorizeTheme(themeMode = themeMode) {
                startDestination?.let { destination ->
                    AppNavGraph(
                        startDestination = destination,
                        viewModelFactory = appComponent.viewModelFactory(),
                        themeMode = themeMode,
                        onThemeModeChange = { themeMode = it },
                        appLanguage = appLanguage,
                        onLanguageChange = { appLanguage = it }
                    )
                }
            }
        }
    }
}