package br.com.carbuapp.veiculos.domain.usecase

import br.com.carbuapp.veiculos.domain.VeiculoCreateRequest
import br.com.carbuapp.veiculos.domain.VeiculoRepository
import br.com.carbuapp.veiculos.domain.model.Veiculo
import javax.inject.Inject

class SaveVeiculoUseCase @Inject constructor(
    private val repository: VeiculoRepository
) {
    suspend fun create(request: VeiculoCreateRequest): Result<Veiculo> =
        repository.create(request)

    suspend fun update(id: Int, request: VeiculoCreateRequest): Result<Veiculo> =
        repository.update(id, request)
}
