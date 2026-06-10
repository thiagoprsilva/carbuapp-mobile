package br.com.carbuapp.ordens.data

import br.com.carbuapp.core.util.parseHttpError
import br.com.carbuapp.ordens.data.local.OSDao
import br.com.carbuapp.ordens.domain.AvariaCreateRequest
import br.com.carbuapp.ordens.domain.LaudoCreateRequest
import br.com.carbuapp.ordens.domain.OSCreateRequest
import br.com.carbuapp.ordens.domain.OSRepository
import br.com.carbuapp.ordens.domain.model.OrdemServico
import br.com.carbuapp.ordens.domain.model.OrdemServicoDetalhe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OSRepositoryImpl @Inject constructor(
    private val apiService: OSApiService,
    private val dao: OSDao
) : OSRepository {

    override fun observeAll(): Flow<List<OrdemServico>> =
        dao.observeAll().map { it.map { e -> e.toDomain() } }

    override fun observeByStatus(status: String): Flow<List<OrdemServico>> =
        dao.observeByStatus(status).map { it.map { e -> e.toDomain() } }

    override suspend fun refresh(veiculoId: Int?): Result<Unit> {
        return try {
            val remote = apiService.list(veiculoId = veiculoId)
            if (veiculoId == null) dao.clearAll()
            dao.insertAll(remote.map { it.toEntity() })
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(parseHttpError(e)))
        }
    }

    override suspend fun getById(id: Int): Result<OrdemServicoDetalhe> {
        return try {
            val remote = apiService.getById(id)
            dao.insertOrReplace(remote.toEntity())
            Result.success(
                OrdemServicoDetalhe(
                    os = remote.toDomain(),
                    temLaudo = remote.laudo != null,
                    totalFotos = remote.fotos.size,
                    totalOrcamentos = remote.orcamentos.size
                )
            )
        } catch (e: Exception) {
            val cached = dao.getById(id)
            if (cached != null) {
                Result.success(
                    OrdemServicoDetalhe(
                        os = cached.toDomain(),
                        temLaudo = false,
                        totalFotos = 0,
                        totalOrcamentos = 0
                    )
                )
            } else {
                Result.failure(Exception(parseHttpError(e)))
            }
        }
    }

    override suspend fun create(request: OSCreateRequest): Result<OrdemServico> {
        return try {
            val created = apiService.create(request.toDto())
            dao.insertOrReplace(created.toEntity())
            Result.success(created.toDomain())
        } catch (e: Exception) {
            Result.failure(Exception(parseHttpError(e)))
        }
    }

    override suspend fun update(id: Int, request: OSCreateRequest): Result<OrdemServico> {
        return try {
            val updated = apiService.update(id, request.toDto())
            dao.insertOrReplace(updated.toEntity())
            Result.success(updated.toDomain())
        } catch (e: Exception) {
            Result.failure(Exception(parseHttpError(e)))
        }
    }

    override suspend fun updateStatus(id: Int, status: String): Result<OrdemServico> {
        return try {
            val updated = apiService.updateStatus(id, OSStatusRequest(status))
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

    private fun OSCreateRequest.toDto() = OSRequest(
        veiculoId = veiculoId,
        categoria = categoria,
        descricao = descricao,
        dataServico = dataServico,
        observacoes = observacoes,
        laudo = laudo?.let { l ->
            LaudoRequest(
                km = l.km,
                nivelCombust = l.nivelCombust,
                observacoes = l.observacoes,
                avarias = l.avarias.map { a -> AvariaRequest(a.zona, a.severidade, a.observacao) }
            )
        }
    )
}
