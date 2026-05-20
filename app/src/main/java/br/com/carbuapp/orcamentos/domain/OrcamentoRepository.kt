package br.com.carbuapp.orcamentos.domain

import br.com.carbuapp.orcamentos.domain.model.Orcamento
import br.com.carbuapp.orcamentos.domain.model.OrcamentoDetalhe
import br.com.carbuapp.orcamentos.domain.model.OrcamentoItem
import kotlinx.coroutines.flow.Flow

interface OrcamentoRepository {
    fun observeAll(): Flow<List<Orcamento>>
    fun observeByStatus(status: String): Flow<List<Orcamento>>
    fun observeByOS(osId: Int): Flow<List<Orcamento>>
    suspend fun refresh(osId: Int? = null, status: String? = null): Result<Unit>
    suspend fun getById(id: Int): Result<OrcamentoDetalhe>
    suspend fun create(osId: Int, itens: List<OrcamentoItemInput>): Result<Orcamento>
    suspend fun update(id: Int, itens: List<OrcamentoItemInput>): Result<Orcamento>
    suspend fun updateStatus(id: Int, status: String): Result<Orcamento>
    suspend fun delete(id: Int): Result<Unit>
}

data class OrcamentoItemInput(
    val descricao: String,
    val qtd: Double,
    val precoUnit: Double
)
