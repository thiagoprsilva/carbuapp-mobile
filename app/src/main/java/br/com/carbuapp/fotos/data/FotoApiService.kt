package br.com.carbuapp.fotos.data

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface FotoApiService {

    @GET("registroTecnico/{osId}/fotos")
    suspend fun list(@Path("osId") osId: Int): List<FotoDto>

    @Multipart
    @POST("registroTecnico/{osId}/fotos")
    suspend fun upload(
        @Path("osId") osId: Int,
        @Part foto: MultipartBody.Part,
        @Part("descricao") descricao: RequestBody? = null,
        @Part("zona") zona: RequestBody? = null
    ): FotoDto

    @DELETE("registroTecnico/{osId}/fotos/{fotoId}")
    suspend fun delete(
        @Path("osId") osId: Int,
        @Path("fotoId") fotoId: Int
    )
}
