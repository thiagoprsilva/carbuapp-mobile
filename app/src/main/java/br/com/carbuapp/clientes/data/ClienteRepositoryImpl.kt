package br.com.carbuapp.clientes.data

import br.com.carbuapp.clientes.domain.ClienteRepository
import br.com.carbuapp.clientes.domain.model.Cliente
import br.com.carbuapp.clientes.data.local.ClienteDao
import br.com.carbuapp.core.util.parseHttpError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClienteRepositoryImpl @Inject constructor(
    private val apiService: ClienteApiService,
    private val dao: ClienteDao
) : ClienteRepository {

    // Emite do Room em tempo real — UI sempre atualizada
    override fun observeAll(): Flow<List<Cliente>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    // Busca da API e atualiza o cache
    override suspend fun refresh(): Result<Unit> {
        return try {
            val remote = apiService.list()
            dao.clearAll()
            dao.insertAll(remote.map { it.toEntity() })
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(parseHttpError(e)))
        }
    }

    override suspend fun getById(id: Int): Result<Cliente> {
        return try {
            val remote = apiService.getById(id)
            dao.insertOrReplace(remote.toEntity())
            Result.success(remote.toDomain())
        } catch (e: Exception) {
            val cached = dao.getById(id)
            if (cached != null) Result.success(cached.toDomain())
            else Result.failure(Exception(parseHttpError(e)))
        }
    }

    override suspend fun create(nome: String, telefone: String?): Result<Cliente> {
        return try {
            val created = apiService.create(ClienteRequest(nome, telefone))
            dao.insertOrReplace(created.toEntity())
            Result.success(created.toDomain())
        } catch (e: Exception) {
            Result.failure(Exception(parseHttpError(e)))
        }
    }

    override suspend fun update(id: Int, nome: String, telefone: String?): Result<Cliente> {
        return try {
            val updated = apiService.update(id, ClienteRequest(nome, telefone))
            dao.insertOrReplace(updated.toEntity())
            Result.success(updated.toDomain())
        } catch (e: Exception) {
            Result.failure(Exception(parseHttpError(e)))
        }
    }

    override suspend fun delete(id: Int): Result<Unit> {
        return try {
            apiService.delete(id)
            dao.deleteById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(parseHttpError(e)))
        }
    }

    override fun search(query: String): Flow<List<Cliente>> =
        dao.search(query).map { list -> list.map { it.toDomain() } }
}
