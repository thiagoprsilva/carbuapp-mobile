package br.com.carbuapp.usuarios.domain

interface UsuarioRepository {
    suspend fun listar(): Result<List<Usuario>>
    suspend fun criar(input: UsuarioCreateInput): Result<Usuario>
    suspend fun atualizar(id: Int, input: UsuarioUpdateInput): Result<Usuario>
    suspend fun resetarSenha(id: Int, novaSenha: String): Result<Unit>
}
