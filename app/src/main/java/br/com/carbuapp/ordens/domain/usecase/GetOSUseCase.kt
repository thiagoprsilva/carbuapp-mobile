package br.com.carbuapp.ordens.domain.usecase

import br.com.carbuapp.ordens.domain.OSRepository
import br.com.carbuapp.ordens.domain.model.OrdemServico
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOSUseCase @Inject constructor(
    private val repository: OSRepository
) {
    fun observeAll(): Flow<List<OrdemServico>> = repository.observeAll()

    fun observeByStatus(status: String): Flow<List<OrdemServico>> =
        repository.observeByStatus(status)

    suspend fun refresh(veiculoId: Int? = null): Result<Unit> =
        repository.refresh(veiculoId)
}
