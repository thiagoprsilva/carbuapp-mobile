package br.com.carbuapp.templates.data

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TemplateApiService {

    @GET("templates")
    suspend fun list(): List<TemplateDto>

    @POST("templates")
    suspend fun create(@Body request: TemplateRequest): TemplateDto

    @PUT("templates/{id}")
    suspend fun update(@Path("id") id: Int, @Body request: TemplateRequest): TemplateDto

    @DELETE("templates/{id}")
    suspend fun delete(@Path("id") id: Int)
}
