package br.com.carbuapp.usuarios.di

import br.com.carbuapp.usuarios.data.UsuarioApiService
import br.com.carbuapp.usuarios.data.UsuarioRepositoryImpl
import br.com.carbuapp.usuarios.domain.UsuarioRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UsuariosModule {

    @Binds
    @Singleton
    abstract fun bindUsuarioRepository(impl: UsuarioRepositoryImpl): UsuarioRepository

    companion object {
        @Provides
        @Singleton
        fun provideUsuarioApiService(retrofit: Retrofit): UsuarioApiService =
            retrofit.create(UsuarioApiService::class.java)
    }
}
