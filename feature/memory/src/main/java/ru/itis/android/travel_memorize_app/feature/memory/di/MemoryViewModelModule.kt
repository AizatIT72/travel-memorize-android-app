package ru.itis.android.travel_memorize_app.feature.memory.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.itis.android.travel_memorize_app.feature.memory.viewmodel.AddMemoryViewModel
import ru.itis.android.travel_memorize_app.feature.memory.viewmodel.EditMemoryViewModel
import ru.itis.android.travel_memorize_app.feature.memory.viewmodel.MemoriesViewModel
import ru.itis.android.travel_memorize_app.feature.memory.viewmodel.MemoryDetailsViewModel
import ru.itis.android.travel_memorize_app.feature.memory.viewmodel.MemorySearchViewModel
import ru.itis.android.travel_memorize_app.utils.di.ViewModelKey

@Module
interface MemoryViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AddMemoryViewModel::class)
    fun bindAddMemoryViewModel(viewModel: AddMemoryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MemoryDetailsViewModel::class)
    fun bindMemoryDetailsViewModel(viewModel: MemoryDetailsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MemorySearchViewModel::class)
    fun bindMemorySearchViewModel(viewModel: MemorySearchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EditMemoryViewModel::class)
    fun bindEditMemoryViewModel(viewModel: EditMemoryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MemoriesViewModel::class)
    fun bindMemoriesViewModel(viewModel: MemoriesViewModel): ViewModel
}