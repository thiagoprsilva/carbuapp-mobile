package br.com.carbuapp.veiculos.domain

import br.com.carbuapp.veiculos.domain.model.TimelineEvento
import br.com.carbuapp.veiculos.domain.model.Veiculo
import kotlinx.coroutines.flow.Flow

interface VeiculoRepository {
    fun observeAll(): Flow<List<Veiculo>>
    fun observeByCliente(clienteId: Int): Flow<List<Veiculo>>
    suspend fun refresh(clienteId: Int? = null): Result<Unit>
    suspend fun getById(id: Int): Result<Veiculo>
    suspend fun getTimeline(id: Int): Result<List<TimelineEvento>>
    suspend fun create(request: VeiculoCreateRequest): Result<Veiculo>
    suspend fun update(id: Int, request: VeiculoCreateRequest): Result<Veiculo>
    suspend fun delete(id: Int): Result<Unit>
    fun search(query: String): Flow<List<Veiculo>>
}

data class VeiculoCreateRequest(
    val placa: String,
    val modelo: String,
    val ano: String?,
    val motor: String?,
    val alimentacao: String?,
    val clienteId: Int
)
