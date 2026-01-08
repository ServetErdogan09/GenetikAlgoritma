package com.serveterdogan.genetikalgoritmamobil.di

import com.serveterdogan.genetikalgoritmamobil.data.repository.FacultyRepositoryImpl
import com.serveterdogan.genetikalgoritmamobil.domain.repository.FacultyRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    // senden interface istedikleri zaman bunu kullan FacultyRepositoryImpl ver
    @Binds
    abstract fun bindFacultyRepository(
        impl : FacultyRepositoryImpl
    ): FacultyRepository
}