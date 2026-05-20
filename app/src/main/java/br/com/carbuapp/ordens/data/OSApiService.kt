package br.com.carbuapp.ordens.data

import retrofit2.http.*

interface OSApiService {

    @GET("registroTecnico")
    suspend fun list(
        @Query("veiculoId") veiculoId: Int? = null,
        @Query("limit") limit: Int? = null
    ): List<OSDto>

    @GET("registroTecnico/{id}")
    suspend fun getById(@Path("id") id: Int): OSDetalheDto

    @POST("registroTecnico")
    suspend fun create(@Body request: OSRequest): OSDetalheDto

    @PUT("registroTecnico/{id}")
    suspend fun update(@Path("id") id: Int, @Body request: OSRequest): OSDetalheDto

    @PATCH("registroTecnico/{id}/status")
    suspend fun updateStatus(@Path("id") id: Int, @Body request: OSStatusRequest): OSDto

    @DELETE("registroTecnico/{id}")
    suspend fun delete(@Path("id") id: Int)
}
