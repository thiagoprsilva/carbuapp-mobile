package br.com.carbuapp.veiculos.domain.usecase

import br.com.carbuapp.veiculos.domain.VeiculoRepository
import br.com.carbuapp.veiculos.domain.model.Veiculo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetVeiculosUseCase @Inject constructor(
    private val repository: VeiculoRepository
) {
    operator fun invoke(): Flow<List<Veiculo>> = repository.observeAll()
    fun byCliente(clienteId: Int): Flow<List<Veiculo>> = repository.observeByCliente(clienteId)
    suspend fun refresh(clienteId: Int? = null): Result<Unit> = repository.refresh(clienteId)
    fun search(query: String): Flow<List<Veiculo>> = repository.search(query)
}
