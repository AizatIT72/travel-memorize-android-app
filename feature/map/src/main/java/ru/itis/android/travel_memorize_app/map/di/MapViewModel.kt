package ru.itis.android.travel_memorize_app.map.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.itis.android.travel_memorize_app.map.viewmodel.MapViewModel
import ru.itis.android.travel_memorize_app.utils.di.ViewModelKey

@Module
interface MapViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MapViewModel::class)
    fun bindMapViewModel(viewModel: MapViewModel): ViewModel
}