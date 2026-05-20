package br.com.carbuapp.oficina.data

import retrofit2.http.GET

interface OficinaApiService {

    // Somente superadmin tem acesso a este endpoint
    @GET("oficinas")
    suspend fun listarTodas(): List<OficinaDto>
}
