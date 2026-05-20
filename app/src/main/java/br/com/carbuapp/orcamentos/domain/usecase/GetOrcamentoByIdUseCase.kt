package br.com.carbuapp.orcamentos.domain.usecase

import br.com.carbuapp.orcamentos.domain.OrcamentoRepository
import br.com.carbuapp.orcamentos.domain.model.OrcamentoDetalhe
import javax.inject.Inject

class GetOrcamentoByIdUseCase @Inject constructor(
    private val repository: OrcamentoRepository
) {
    suspend operator fun invoke(id: Int): Result<OrcamentoDetalhe> = repository.getById(id)
}
