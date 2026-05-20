package br.com.carbuapp.ordens.domain.usecase

import br.com.carbuapp.ordens.domain.OSRepository
import br.com.carbuapp.ordens.domain.model.OrdemServico
import javax.inject.Inject

class UpdateOSStatusUseCase @Inject constructor(
    private val repository: OSRepository
) {
    suspend operator fun invoke(id: Int, status: String): Result<OrdemServico> =
        repository.updateStatus(id, status)
}
