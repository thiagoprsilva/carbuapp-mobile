package br.com.carbuapp.orcamentos.data

import okhttp3.ResponseBody
import retrofit2.http.*

interface OrcamentoApiService {

    @GET("orcamento")
    suspend fun list(
        @Query("veiculoId")          veiculoId: Int?    = null,
        @Query("status")             status: String?    = null,
        @Query("registroTecnicoId")  osId: Int?         = null
    ): List<OrcamentoDto>

    @GET("orcamento/{id}")
    suspend fun getById(@Path("id") id: Int): OrcamentoDto

    @POST("orcamento")
    suspend fun create(@Body request: OrcamentoCreateRequest): OrcamentoDto

    @PUT("orcamento/{id}")
    suspend fun update(@Path("id") id: Int, @Body request: OrcamentoUpdateRequest): OrcamentoDto

    @PATCH("orcamento/{id}/status")
    suspend fun updateStatus(@Path("id") id: Int, @Body request: OrcamentoStatusRequest): OrcamentoDto

    @DELETE("orcamento/{id}")
    suspend fun delete(@Path("id") id: Int)

    @Streaming
    @GET("orcamento/{id}/pdf")
    suspend fun getPdf(@Path("id") id: Int): ResponseBody
}
