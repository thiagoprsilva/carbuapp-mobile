package br.com.carbuapp.clientes.di

import br.com.carbuapp.clientes.data.ClienteApiService
import br.com.carbuapp.clientes.data.ClienteRepositoryImpl
import br.com.carbuapp.clientes.data.local.ClienteDao
import br.com.carbuapp.clientes.domain.ClienteRepository
import br.com.carbuapp.core.database.AppDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ClienteModule {

    @Binds
    @Singleton
    abstract fun bindClienteRepository(impl: ClienteRepositoryImpl): ClienteRepository

    companion object {

        @Provides
        @Singleton
        fun provideClienteApiService(retrofit: Retrofit): ClienteApiService =
            retrofit.create(ClienteApiService::class.java)

        @Provides
        fun provideClienteDao(db: AppDatabase): ClienteDao = db.clienteDao()
    }
}
