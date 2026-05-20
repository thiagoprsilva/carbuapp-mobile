package br.com.carbuapp.oficina.di

import br.com.carbuapp.oficina.data.OficinaApiService
import br.com.carbuapp.oficina.data.OficinaRepositoryImpl
import br.com.carbuapp.oficina.domain.OficinaRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class OficinaModule {

    @Binds
    @Singleton
    abstract fun bindOficinaRepository(impl: OficinaRepositoryImpl): OficinaRepository

    companion object {
        @Provides
        @Singleton
        fun provideOficinaApiService(retrofit: Retrofit): OficinaApiService =
            retrofit.create(OficinaApiService::class.java)
    }
}
