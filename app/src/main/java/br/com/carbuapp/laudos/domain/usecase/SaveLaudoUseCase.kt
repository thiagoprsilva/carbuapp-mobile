package br.com.carbuapp.laudos.domain.usecase

import br.com.carbuapp.laudos.domain.LaudoRepository
import br.com.carbuapp.laudos.domain.LaudoSaveRequest
import br.com.carbuapp.laudos.domain.model.Laudo
import javax.inject.Inject

class SaveLaudoUseCase @Inject constructor(
    private val repository: LaudoRepository
) {
    suspend operator fun invoke(request: LaudoSaveRequest): Laudo =
        repository.save(request)
}
