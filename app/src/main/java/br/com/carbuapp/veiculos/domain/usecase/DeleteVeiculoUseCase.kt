package br.com.carbuapp.veiculos.domain.usecase

import br.com.carbuapp.veiculos.domain.VeiculoRepository
import javax.inject.Inject

class DeleteVeiculoUseCase @Inject constructor(
    private val repository: VeiculoRepository
) {
    suspend operator fun invoke(id: Int): Result<Unit> = repository.delete(id)
}
