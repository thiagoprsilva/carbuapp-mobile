package br.com.carbuapp.veiculos.data

import br.com.carbuapp.core.util.parseHttpError
import br.com.carbuapp.veiculos.data.local.VeiculoDao
import br.com.carbuapp.veiculos.domain.VeiculoCreateRequest
import br.com.carbuapp.veiculos.domain.VeiculoRepository
import br.com.carbuapp.veiculos.domain.model.TimelineEvento
import br.com.carbuapp.veiculos.domain.model.Veiculo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VeiculoRepositoryImpl @Inject constructor(
    private val apiService: VeiculoApiService,
    private val dao: VeiculoDao
) : VeiculoRepository {

    override fun observeAll(): Flow<List<Veiculo>> =
        dao.observeAll().map { it.map { e -> e.toDomain() } }

    override fun observeByCliente(clienteId: Int): Flow<List<Veiculo>> =
        dao.observeByCliente(clienteId).map { it.map { e -> e.toDomain() } }

    override suspend fun refresh(clienteId: Int?): Result<Unit> {
        return try {
            val remote = apiService.list(clienteId)
            if (clienteId == null) dao.clearAll()
            dao.insertAll(remote.map { it.toEntity() })
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(parseHttpError(e)))
        }
    }

    override suspend fun getById(id: Int): Result<Veiculo> {
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

    override suspend fun getTimeline(id: Int): Result<List<TimelineEvento>> {
        return try {
            val jsonArray = apiService.getTimeline(id)
            val eventos = mutableListOf<TimelineEvento>()
            for (i in 0 until jsonArray.size()) {
                val obj = jsonArray[i].asJsonObject
                when (obj.get("tipo").asString) {
                    "registro" -> eventos.add(
                        TimelineEvento.Registro(
                            id = obj.get("id").asInt,
                            data = obj.get("data").asString,
                            categoria = obj.get("categoria").asString,
                            descricao = obj.get("descricao").asString,
                            observacoes = obj.get("observacoes")?.takeIf { !it.isJsonNull }?.asString
                        )
                    )
                    "orcamento" -> eventos.add(
                        TimelineEvento.Orcamento(
                            id = obj.get("id").asInt,
                            data = obj.get("data").asString,
                            numero = obj.get("numero").asInt,
                            total = obj.get("total").asDouble
                        )
                    )
                }
            }
            Result.success(eventos)
        } catch (e: Exception) {
            Result.failure(Exception(parseHttpError(e)))
        }
    }

    override suspend fun create(request: VeiculoCreateRequest): Result<Veiculo> {
        return try {
            val created = apiService.create(request.toDto())
            dao.insertOrReplace(created.toEntity())
            Result.success(created.toDomain())
        } catch (e: Exception) {
            Result.failure(Exception(parseHttpError(e)))
        }
    }

    override suspend fun update(id: Int, request: VeiculoCreateRequest): Result<Veiculo> {
        return try {
            val updated = apiService.update(id, request.toDto())
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

    override fun search(query: String): Flow<List<Veiculo>> =
        dao.search(query).map { it.map { e -> e.toDomain() } }

    private fun VeiculoCreateRequest.toDto() = VeiculoRequest(
        placa = placa,
        modelo = modelo,
        ano = ano,
        motor = motor,
        alimentacao = alimentacao,
        clienteId = clienteId
    )
}
