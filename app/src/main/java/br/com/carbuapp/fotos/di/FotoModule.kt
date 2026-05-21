package br.com.carbuapp.fotos.di

import br.com.carbuapp.fotos.data.FotoApiService
import br.com.carbuapp.fotos.data.FotoRepositoryImpl
import br.com.carbuapp.fotos.domain.FotoRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FotoModule {

    @Binds
    @Singleton
    abstract fun bindFotoRepository(impl: FotoRepositoryImpl): FotoRepository

    companion object {
        @Provides
        @Singleton
        fun provideFotoApiService(retrofit: Retrofit): FotoApiService =
            retrofit.create(FotoApiService::class.java)
    }
}
