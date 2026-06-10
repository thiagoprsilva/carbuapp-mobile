package br.com.carbuapp.search.di

import br.com.carbuapp.search.data.SearchApiService
import br.com.carbuapp.search.data.SearchRepositoryImpl
import br.com.carbuapp.search.domain.SearchRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SearchModule {

    @Binds
    @Singleton
    abstract fun bindSearchRepository(impl: SearchRepositoryImpl): SearchRepository

    companion object {
        @Provides
        @Singleton
        fun provideSearchApiService(retrofit: Retrofit): SearchApiService =
            retrofit.create(SearchApiService::class.java)
    }
}
