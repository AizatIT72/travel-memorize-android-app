package ru.itis.android.travel_memorize_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import ru.itis.android.travel_memorize_app.feature.map.presentation.MapScreen
import ru.itis.android.travel_memorize_app.feature.map.presentation.MapViewModel
import ru.itis.android.travel_memorize_app.ui.theme.Travel_memorize_appTheme
import javax.inject.Inject

class MapActivity : ComponentActivity() {

    @Inject
    lateinit var viewModelFactory: MapViewModel.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as TravelApp).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Travel_memorize_appTheme {
                val viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return viewModelFactory.create() as T
                    }
                })[MapViewModel::class.java]
                
                MapScreen(viewModel = viewModel)
            }
        }
    }
}
