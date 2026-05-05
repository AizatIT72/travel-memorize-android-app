package ru.itis.android.travel_memorize_app.core.data.di

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.itis.android.travel_memorize_app.core.data.repository.MapRepositoryImpl
import ru.itis.android.travel_memorize_app.core.data.utils.FirestoreMapper
import ru.itis.android.travel_memorize_app.core.data.utils.InMemoryCache
import ru.itis.android.travel_memorize_app.core.domain.repository.map.MapRepository
import javax.inject.Singleton

@Module
interface MapDataBindModule {

    @Binds
    @Singleton
    fun bindMapRepository(impl: MapRepositoryImpl): MapRepository
}
@Module
object MapDataModule {

    @Provides
    @Singleton
    fun provideFirestoreMapper(firestore: FirebaseFirestore): FirestoreMapper {
        return FirestoreMapper(firestore)
    }

    @Provides
    @Singleton
    fun provideCache(): InMemoryCache = InMemoryCache()
}
