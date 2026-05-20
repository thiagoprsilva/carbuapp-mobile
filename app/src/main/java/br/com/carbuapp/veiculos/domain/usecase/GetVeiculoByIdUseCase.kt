package br.com.carbuapp.veiculos.domain.usecase

import br.com.carbuapp.veiculos.domain.VeiculoRepository
import br.com.carbuapp.veiculos.domain.model.TimelineEvento
import br.com.carbuapp.veiculos.domain.model.Veiculo
import javax.inject.Inject

class GetVeiculoByIdUseCase @Inject constructor(
    private val repository: VeiculoRepository
) {
    suspend operator fun invoke(id: Int): Result<Veiculo> = repository.getById(id)
    suspend fun timeline(id: Int): Result<List<TimelineEvento>> = repository.getTimeline(id)
}
