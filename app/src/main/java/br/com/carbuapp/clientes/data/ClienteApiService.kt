package br.com.carbuapp.clientes.data

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ClienteApiService {

    @GET("clientes")
    suspend fun list(): List<ClienteDto>

    @GET("clientes/{id}")
    suspend fun getById(@Path("id") id: Int): ClienteDto

    @POST("clientes")
    suspend fun create(@Body request: ClienteRequest): ClienteDto

    @PUT("clientes/{id}")
    suspend fun update(
        @Path("id") id: Int,
        @Body request: ClienteRequest
    ): ClienteDto

    @DELETE("clientes/{id}")
    suspend fun delete(@Path("id") id: Int)
}
