package br.com.carbuapp.fotos.domain.usecase

import br.com.carbuapp.fotos.domain.FotoRepository
import br.com.carbuapp.fotos.domain.model.Foto
import javax.inject.Inject

class GetFotosUseCase @Inject constructor(private val repo: FotoRepository) {
    suspend operator fun invoke(osId: Int): List<Foto> = repo.list(osId)
}
