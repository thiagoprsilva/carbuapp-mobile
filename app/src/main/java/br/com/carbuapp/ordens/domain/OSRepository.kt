package br.com.carbuapp.ordens.domain

import br.com.carbuapp.ordens.domain.model.OrdemServico
import br.com.carbuapp.ordens.domain.model.OrdemServicoDetalhe
import kotlinx.coroutines.flow.Flow

interface OSRepository {
    fun observeAll(): Flow<List<OrdemServico>>
    fun observeByStatus(status: String): Flow<List<OrdemServico>>
    suspend fun refresh(veiculoId: Int? = null): Result<Unit>
    suspend fun getById(id: Int): Result<OrdemServicoDetalhe>
    suspend fun create(request: OSCreateRequest): Result<OrdemServico>
    suspend fun update(id: Int, request: OSCreateRequest): Result<OrdemServico>
    suspend fun updateStatus(id: Int, status: String): Result<OrdemServico>
    suspend fun delete(id: Int): Result<Unit>
}

data class AvariaCreateRequest(
    val zona: String,
    val severidade: String?,
    val observacao: String?
)

data class LaudoCreateRequest(
    val km: Int?,
    val nivelCombust: String?,
    val observacoes: String?,
    val avarias: List<AvariaCreateRequest>
)

data class OSCreateRequest(
    val veiculoId: Int,
    val categoria: String,
    val descricao: String,
    val dataServico: String,
    val observacoes: String?,
    val laudo: LaudoCreateRequest? = null
)
