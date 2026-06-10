package br.com.carbuapp.search.domain

interface SearchRepository {
    suspend fun search(query: String): List<SearchResult>
}
