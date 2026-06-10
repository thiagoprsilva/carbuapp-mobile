package br.com.carbuapp.oficina.data

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface OficinaApiService {

    // Somente superadmin tem acesso a este endpoint
    @GET("oficinas")
    suspend fun listarTodas(): List<OficinaDto>

    @GET("oficinas/{id}")
    suspend fun getById(@Path("id") id: Int): OficinaDto

    @PATCH("oficinas/{id}")
    suspend fun update(
        @Path("id") id: Int,
        @Body body: OficinaUpdateRequest
    ): OficinaDto

    @Multipart
    @POST("oficinas/{id}/logo")
    suspend fun uploadLogo(
        @Path("id") id: Int,
        @Part logo: MultipartBody.Part
    ): OficinaDto

    @DELETE("oficinas/{id}/logo")
    suspend fun deleteLogo(@Path("id") id: Int)
}
