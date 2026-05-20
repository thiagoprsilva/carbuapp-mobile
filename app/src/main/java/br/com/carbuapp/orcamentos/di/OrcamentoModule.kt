package br.com.carbuapp.orcamentos.di

import br.com.carbuapp.orcamentos.data.OrcamentoApiService
import br.com.carbuapp.orcamentos.data.OrcamentoRepositoryImpl
import br.com.carbuapp.orcamentos.domain.OrcamentoRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class OrcamentoModule {

    @Binds
    @Singleton
    abstract fun bindOrcamentoRepository(impl: OrcamentoRepositoryImpl): OrcamentoRepository

    companion object {
        @Provides
        @Singleton
        fun provideOrcamentoApiService(retrofit: Retrofit): OrcamentoApiService =
            retrofit.create(OrcamentoApiService::class.java)
    }
}
