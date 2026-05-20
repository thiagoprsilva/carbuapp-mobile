package br.com.carbuapp.orcamentos.domain.usecase

import br.com.carbuapp.orcamentos.domain.OrcamentoRepository
import br.com.carbuapp.orcamentos.domain.model.Orcamento
import javax.inject.Inject

class UpdateOrcamentoStatusUseCase @Inject constructor(
    private val repository: OrcamentoRepository
) {
    suspend operator fun invoke(id: Int, status: String): Result<Orcamento> =
        repository.updateStatus(id, status)
}
