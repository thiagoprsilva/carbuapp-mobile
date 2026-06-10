package br.com.carbuapp.usuarios.data

import retrofit2.http.*

interface UsuarioApiService {

    @GET("usuarios")
    suspend fun listar(): List<UsuarioDto>

    @POST("usuarios")
    suspend fun criar(@Body body: UsuarioCreateRequest): UsuarioDto

    @PATCH("usuarios/{id}")
    suspend fun atualizar(
        @Path("id") id: Int,
        @Body body: UsuarioUpdateRequest
    ): UsuarioDto

    @POST("usuarios/{id}/reset-senha")
    suspend fun resetarSenha(
        @Path("id") id: Int,
        @Body body: ResetSenhaRequest
    )
}
