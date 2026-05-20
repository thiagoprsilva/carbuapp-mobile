package br.com.carbuapp.clientes.domain.usecase

import br.com.carbuapp.clientes.domain.ClienteRepository
import br.com.carbuapp.clientes.domain.model.Cliente
import javax.inject.Inject

class GetClienteByIdUseCase @Inject constructor(
    private val repository: ClienteRepository
) {
    suspend operator fun invoke(id: Int): Result<Cliente> = repository.getById(id)
}
