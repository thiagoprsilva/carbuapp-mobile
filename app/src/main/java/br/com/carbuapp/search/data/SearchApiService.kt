package br.com.carbuapp.search.data

import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApiService {
    // Único endpoint com prefixo /api/ no backend
    @GET("api/search")
    suspend fun search(@Query("q") query: String): SearchResponseDto
}
