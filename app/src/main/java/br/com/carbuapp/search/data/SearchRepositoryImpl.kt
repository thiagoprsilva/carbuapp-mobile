package br.com.carbuapp.search.data

import br.com.carbuapp.search.domain.SearchRepository
import br.com.carbuapp.search.domain.SearchResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepositoryImpl @Inject constructor(
    private val api: SearchApiService
) : SearchRepository {

    override suspend fun search(query: String): List<SearchResult> {
        if (query.isBlank()) return emptyList()
        return api.search(query).results.map { it.toDomain() }
    }
}
