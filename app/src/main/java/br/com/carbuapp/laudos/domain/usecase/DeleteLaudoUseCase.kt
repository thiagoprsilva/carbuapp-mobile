package br.com.carbuapp.laudos.domain.usecase

import br.com.carbuapp.laudos.domain.LaudoRepository
import javax.inject.Inject

class DeleteLaudoUseCase @Inject constructor(
    private val repository: LaudoRepository
) {
    suspend operator fun invoke(osId: Int) = repository.delete(osId)
}
