package br.com.carbuapp.orcamentos.domain.usecase

import br.com.carbuapp.orcamentos.domain.OrcamentoRepository
import br.com.carbuapp.orcamentos.domain.model.Orcamento
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOrcamentosUseCase @Inject constructor(
    private val repository: OrcamentoRepository
) {
    fun observeAll(): Flow<List<Orcamento>> = repository.observeAll()
    fun observeByStatus(status: String): Flow<List<Orcamento>> = repository.observeByStatus(status)
    fun observeByOS(osId: Int): Flow<List<Orcamento>> = repository.observeByOS(osId)
    suspend fun refresh(osId: Int? = null, status: String? = null): Result<Unit> =
        repository.refresh(osId, status)
}
