package br.com.carbuapp.clientes.data

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ClienteApiService {

    @GET("api/clientes")
    suspend fun list(): List<ClienteDto>

    @GET("api/clientes/{id}")
    suspend fun getById(@Path("id") id: Int): ClienteDto

    @POST("api/clientes")
    suspend fun create(@Body request: ClienteRequest): ClienteDto

    @PUT("api/clientes/{id}")
    suspend fun update(
        @Path("id") id: Int,
        @Body request: ClienteRequest
    ): ClienteDto

    @DELETE("api/clientes/{id}")
    suspend fun delete(@Path("id") id: Int)
}
