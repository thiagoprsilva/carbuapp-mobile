package br.com.carbuapp.ordens.di

import br.com.carbuapp.ordens.data.OSApiService
import br.com.carbuapp.ordens.data.OSRepositoryImpl
import br.com.carbuapp.ordens.domain.OSRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class OSModule {

    @Binds
    @Singleton
    abstract fun bindOSRepository(impl: OSRepositoryImpl): OSRepository

    companion object {
        @Provides
        @Singleton
        fun provideOSApiService(retrofit: Retrofit): OSApiService =
            retrofit.create(OSApiService::class.java)
    }
}
