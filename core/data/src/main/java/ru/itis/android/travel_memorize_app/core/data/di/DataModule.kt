package ru.itis.android.travel_memorize_app.core.data.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.itis.android.travel_memorize_app.core.data.repository.AuthRepositoryImpl
import ru.itis.android.travel_memorize_app.core.domain.repository.AuthRepository
import javax.inject.Singleton

@Module
interface DataBindModule {
    @Binds
    @Singleton
    fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}

@Module
object DataModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
}
