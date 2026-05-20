package br.com.carbuapp.veiculos.data

import com.google.gson.JsonArray
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface VeiculoApiService {

    @GET("veiculos")
    suspend fun list(@Query("clienteId") clienteId: Int? = null): List<VeiculoDto>

    @GET("veiculos/{id}")
    suspend fun getById(@Path("id") id: Int): VeiculoDto

    @GET("veiculos/{id}/timeline")
    suspend fun getTimeline(@Path("id") id: Int): JsonArray   // deserialização manual por tipo

    @POST("veiculos")
    suspend fun create(@Body request: VeiculoRequest): VeiculoDto

    @PUT("veiculos/{id}")
    suspend fun update(
        @Path("id") id: Int,
        @Body request: VeiculoRequest
    ): VeiculoDto

    @DELETE("veiculos/{id}")
    suspend fun delete(@Path("id") id: Int)
}
