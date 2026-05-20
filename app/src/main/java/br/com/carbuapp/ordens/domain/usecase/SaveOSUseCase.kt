package br.com.carbuapp.ordens.domain.usecase

import br.com.carbuapp.ordens.domain.OSCreateRequest
import br.com.carbuapp.ordens.domain.OSRepository
import br.com.carbuapp.ordens.domain.model.OrdemServico
import javax.inject.Inject

class SaveOSUseCase @Inject constructor(
    private val repository: OSRepository
) {
    suspend operator fun invoke(id: Int?, request: OSCreateRequest): Result<OrdemServico> =
        if (id == null) repository.create(request)
        else repository.update(id, request)
}
