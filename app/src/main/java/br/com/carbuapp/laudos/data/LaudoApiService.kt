package br.com.carbuapp.laudos.data

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface LaudoApiService {

    @GET("registroTecnico/{osId}/laudo")
    suspend fun get(@Path("osId") osId: Int): LaudoResponseDto

    /** Upsert — cria ou atualiza o laudo da OS */
    @POST("registroTecnico/{osId}/laudo")
    suspend fun save(
        @Path("osId") osId: Int,
        @Body request: LaudoRequest
    ): LaudoResponseDto

    @DELETE("registroTecnico/{osId}/laudo")
    suspend fun delete(@Path("osId") osId: Int)
}
