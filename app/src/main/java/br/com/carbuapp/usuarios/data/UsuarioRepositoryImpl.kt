package br.com.carbuapp.usuarios.data

import br.com.carbuapp.core.util.parseHttpError
import br.com.carbuapp.usuarios.domain.Usuario
import br.com.carbuapp.usuarios.domain.UsuarioCreateInput
import br.com.carbuapp.usuarios.domain.UsuarioRepository
import br.com.carbuapp.usuarios.domain.UsuarioUpdateInput
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsuarioRepositoryImpl @Inject constructor(
    private val api: UsuarioApiService
) : UsuarioRepository {

    override suspend fun listar(): Result<List<Usuario>> = try {
        Result.success(api.listar().map { it.toDomain() })
    } catch (e: Exception) {
        Result.failure(Exception(parseHttpError(e)))
    }

    override suspend fun criar(input: UsuarioCreateInput): Result<Usuario> = try {
        val body = UsuarioCreateRequest(
            nome  = input.nome,
            email = input.email,
            senha = input.senha,
            role  = input.role
        )
        Result.success(api.criar(body).toDomain())
    } catch (e: Exception) {
        Result.failure(Exception(parseHttpError(e)))
    }

    override suspend fun atualizar(id: Int, input: UsuarioUpdateInput): Result<Usuario> = try {
        val body = UsuarioUpdateRequest(
            nome  = input.nome,
            email = input.email,
            role  = input.role,
            ativo = input.ativo
        )
        Result.success(api.atualizar(id, body).toDomain())
    } catch (e: Exception) {
        Result.failure(Exception(parseHttpError(e)))
    }

    override suspend fun resetarSenha(id: Int, novaSenha: String): Result<Unit> = try {
        api.resetarSenha(id, ResetSenhaRequest(novaSenha))
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(Exception(parseHttpError(e)))
    }
}
