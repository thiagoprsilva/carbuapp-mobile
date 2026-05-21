package br.com.carbuapp.laudos.domain.usecase

import br.com.carbuapp.laudos.domain.LaudoRepository
import br.com.carbuapp.laudos.domain.model.Laudo
import javax.inject.Inject

class GetLaudoUseCase @Inject constructor(
    private val repository: LaudoRepository
) {
    suspend operator fun invoke(osId: Int): Laudo? = repository.get(osId)
}
