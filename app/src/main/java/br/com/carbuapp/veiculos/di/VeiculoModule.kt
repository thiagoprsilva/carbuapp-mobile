package br.com.carbuapp.veiculos.di

import br.com.carbuapp.core.database.AppDatabase
import br.com.carbuapp.veiculos.data.VeiculoApiService
import br.com.carbuapp.veiculos.data.VeiculoRepositoryImpl
import br.com.carbuapp.veiculos.data.local.VeiculoDao
import br.com.carbuapp.veiculos.domain.VeiculoRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class VeiculoModule {

    @Binds
    @Singleton
    abstract fun bindVeiculoRepository(impl: VeiculoRepositoryImpl): VeiculoRepository

    companion object {

        @Provides
        @Singleton
        fun provideVeiculoApiService(retrofit: Retrofit): VeiculoApiService =
            retrofit.create(VeiculoApiService::class.java)

        @Provides
        fun provideVeiculoDao(db: AppDatabase): VeiculoDao = db.veiculoDao()
    }
}
