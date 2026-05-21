package br.com.carbuapp.templates.di

import br.com.carbuapp.templates.data.TemplateApiService
import br.com.carbuapp.templates.data.TemplateRepositoryImpl
import br.com.carbuapp.templates.domain.TemplateRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TemplateModule {

    @Binds
    @Singleton
    abstract fun bindTemplateRepository(impl: TemplateRepositoryImpl): TemplateRepository

    companion object {
        @Provides
        @Singleton
        fun provideTemplateApiService(retrofit: Retrofit): TemplateApiService =
            retrofit.create(TemplateApiService::class.java)
    }
}
