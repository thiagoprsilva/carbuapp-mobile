package br.com.carbuapp.laudos.di

import br.com.carbuapp.laudos.data.LaudoApiService
import br.com.carbuapp.laudos.data.LaudoRepositoryImpl
import br.com.carbuapp.laudos.domain.LaudoRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LaudoModule {

    @Binds
    @Singleton
    abstract fun bindLaudoRepository(impl: LaudoRepositoryImpl): LaudoRepository

    companion object {

        @Provides
        @Singleton
        fun provideLaudoApiService(retrofit: Retrofit): LaudoApiService =
            retrofit.create(LaudoApiService::class.java)
    }
}
