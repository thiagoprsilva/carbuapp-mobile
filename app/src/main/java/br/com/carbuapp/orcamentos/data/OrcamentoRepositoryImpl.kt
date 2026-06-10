package br.com.carbuapp.orcamentos.data

import br.com.carbuapp.core.util.parseHttpError
import br.com.carbuapp.orcamentos.data.local.OrcamentoDao
import br.com.carbuapp.orcamentos.domain.OrcamentoItemInput
import br.com.carbuapp.orcamentos.domain.OrcamentoRepository
import br.com.carbuapp.orcamentos.domain.model.Orcamento
import br.com.carbuapp.orcamentos.domain.model.OrcamentoDetalhe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrcamentoRepositoryImpl @Inject constructor(
    private val apiService: OrcamentoApiService,
    private val dao: OrcamentoDao
) : OrcamentoRepository {

    override fun observeAll(): Flow<List<Orcamento>> =
        dao.observeAll().map { it.map { e -> e.toDomain() } }

    override fun observeByStatus(status: String): Flow<List<Orcamento>> =
        dao.observeByStatus(status).map { it.map { e -> e.toDomain() } }

    override fun observeByOS(osId: Int): Flow<List<Orcamento>> =
        dao.observeByOS(osId).map { it.map { e -> e.toDomain() } }

    override suspend fun refresh(osId: Int?, status: String?): Result<Unit> {
        return try {
            val remote = apiService.list(osId = osId, status = status)
            if (osId == null && status == null) dao.clearAll()
            dao.insertAll(remote.map { it.toEntity() })
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(parseHttpError(e)))
        }
    }

    override suspend fun getById(id: Int): Result<OrcamentoDetalhe> {
        return try {
            val remote = apiService.getById(id)
            dao.insertOrReplace(remote.toEntity())
            Result.success(remote.toDetalhe())
        } catch (e: Exception) {
            val cached = dao.getById(id)
            if (cached != null) Result.success(OrcamentoDetalhe(cached.toDomain(), emptyList()))
            else Result.failure(Exception(parseHttpError(e)))
        }
    }

    override suspend fun create(osId: Int, itens: List<OrcamentoItemInput>): Result<Orcamento> {
        return try {
            val created = apiService.create(
                OrcamentoCreateRequest(
                    registroTecnicoId = osId,
                    itens = itens.map { it.toDto() }
                )
            )
            dao.insertOrReplace(created.toEntity())
            Result.success(created.toDomain())
        } catch (e: Exception) {
            Result.failure(Exception(parseHttpError(e)))
        }
    }

    override suspend fun update(id: Int, itens: List<OrcamentoItemInput>): Result<Orcamento> {
        return try {
            val updated = apiService.update(
                id,
                OrcamentoUpdateRequest(itens = itens.map { it.toDto() })
            )
            dao.insertOrReplace(updated.toEntity())
            Result.success(updated.toDomain())
        } catch (e: Exception) {
            Result.failure(Exception(parseHttpError(e)))
        }
    }

    override suspend fun updateStatus(id: Int, status: String): Result<Orcamento> {
        return try {
            val updated = apiService.updateStatus(id, OrcamentoStatusRequest(status))
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

    override suspend fun getPdf(id: Int): Result<ByteArray> {
        return try {
            val body = apiService.getPdf(id)
            Result.success(body.bytes())
        } catch (e: Exception) {
            Result.failure(Exception(parseHttpError(e)))
        }
    }

    private fun OrcamentoItemInput.toDto() = OrcamentoItemRequest(
        descricao = descricao,
        qtd = qtd,
        precoUnit = precoUnit
    )
}
