package br.com.carbuapp.clientes.domain.usecase

import br.com.carbuapp.clientes.domain.ClienteRepository
import br.com.carbuapp.clientes.domain.model.Cliente
import javax.inject.Inject

class SaveClienteUseCase @Inject constructor(
    private val repository: ClienteRepository
) {
    suspend fun create(nome: String, telefone: String?): Result<Cliente> =
        repository.create(nome, telefone)

    suspend fun update(id: Int, nome: String, telefone: String?): Result<Cliente> =
        repository.update(id, nome, telefone)
}
