package br.com.carbuapp.clientes.domain.usecase

import br.com.carbuapp.clientes.domain.ClienteRepository
import javax.inject.Inject

class DeleteClienteUseCase @Inject constructor(
    private val repository: ClienteRepository
) {
    suspend operator fun invoke(id: Int): Result<Unit> = repository.delete(id)
}
