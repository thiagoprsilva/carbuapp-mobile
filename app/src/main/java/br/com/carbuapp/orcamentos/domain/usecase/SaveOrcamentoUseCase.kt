package br.com.carbuapp.orcamentos.domain.usecase

import br.com.carbuapp.orcamentos.domain.OrcamentoItemInput
import br.com.carbuapp.orcamentos.domain.OrcamentoRepository
import br.com.carbuapp.orcamentos.domain.model.Orcamento
import javax.inject.Inject

class SaveOrcamentoUseCase @Inject constructor(
    private val repository: OrcamentoRepository
) {
    suspend operator fun invoke(
        id: Int?,
        osId: Int,
        itens: List<OrcamentoItemInput>
    ): Result<Orcamento> =
        if (id == null) repository.create(osId, itens)
        else repository.update(id, itens)
}
