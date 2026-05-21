package br.com.carbuapp.fotos.domain.usecase

import br.com.carbuapp.fotos.domain.FotoRepository
import javax.inject.Inject

class DeleteFotoUseCase @Inject constructor(private val repo: FotoRepository) {
    suspend operator fun invoke(osId: Int, fotoId: Int) = repo.delete(osId, fotoId)
}
