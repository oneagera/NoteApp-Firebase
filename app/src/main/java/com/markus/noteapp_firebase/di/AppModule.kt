package com.markus.noteapp_firebase.di

import com.markus.noteapp_firebase.domain.repository.AuthRepository
import com.markus.noteapp_firebase.domain.repository.StorageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)

object AppModule {
    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository {
        return AuthRepository()
    }

    @Provides
    @Singleton
    fun provideStorageRepository(): StorageRepository {
        return StorageRepository()
    }
}