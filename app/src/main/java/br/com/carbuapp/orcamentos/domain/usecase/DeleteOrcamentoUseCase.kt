package br.com.carbuapp.orcamentos.domain.usecase

import br.com.carbuapp.orcamentos.domain.OrcamentoRepository
import javax.inject.Inject

class DeleteOrcamentoUseCase @Inject constructor(
    private val repository: OrcamentoRepository
) {
    suspend operator fun invoke(id: Int): Result<Unit> = repository.delete(id)
}
