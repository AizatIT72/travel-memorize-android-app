package ru.itis.android.travel_memorize_app.core.data.di

import com.google.firebase.storage.FirebaseStorage
import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.itis.android.travel_memorize_app.core.data.repository.MemoryRepositoryImpl
import ru.itis.android.travel_memorize_app.core.domain.repository.memory.MemoryRepository
import javax.inject.Singleton


@Module
interface MemoryRepositoryModule {
    @Binds
    fun bindMemoryRepository(
        impl: MemoryRepositoryImpl
    ): MemoryRepository
}


@Module
object MemoryDataModule {

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

}