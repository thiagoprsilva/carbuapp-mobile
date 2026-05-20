package br.com.carbuapp.auth.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("auth/me")
    suspend fun me(): UserDto

    @PATCH("auth/senha")
    suspend fun alterarSenha(@Body request: AlteraSenhaRequest): MessageResponse
}
