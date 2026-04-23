package ru.itis.android.travel_memorize_app

import android.app.Application
import ru.itis.android.travel_memorize_app.di.AppComponent
import ru.itis.android.travel_memorize_app.di.DaggerAppComponent

class TravelApp : Application() {
    
    val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder()
            .build()
    }
}
