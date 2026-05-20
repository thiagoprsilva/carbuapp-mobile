package br.com.carbuapp.ordens.domain.usecase

import br.com.carbuapp.ordens.domain.OSRepository
import br.com.carbuapp.ordens.domain.model.OrdemServicoDetalhe
import javax.inject.Inject

class GetOSByIdUseCase @Inject constructor(
    private val repository: OSRepository
) {
    suspend operator fun invoke(id: Int): Result<OrdemServicoDetalhe> =
        repository.getById(id)
}
