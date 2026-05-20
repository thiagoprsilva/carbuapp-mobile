package br.com.carbuapp.clientes.domain.usecase

import br.com.carbuapp.clientes.domain.ClienteRepository
import br.com.carbuapp.clientes.domain.model.Cliente
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetClientesUseCase @Inject constructor(
    private val repository: ClienteRepository
) {
    operator fun invoke(): Flow<List<Cliente>> = repository.observeAll()
    suspend fun refresh(): Result<Unit> = repository.refresh()
    fun search(query: String): Flow<List<Cliente>> = repository.search(query)
}
