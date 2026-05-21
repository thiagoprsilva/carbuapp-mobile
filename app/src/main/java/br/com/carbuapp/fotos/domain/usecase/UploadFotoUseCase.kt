package br.com.carbuapp.fotos.domain.usecase

import android.net.Uri
import br.com.carbuapp.fotos.domain.FotoRepository
import br.com.carbuapp.fotos.domain.model.Foto
import javax.inject.Inject

class UploadFotoUseCase @Inject constructor(private val repo: FotoRepository) {
    suspend operator fun invoke(osId: Int, uri: Uri, descricao: String? = null, zona: String? = null): Foto =
        repo.upload(osId, uri, descricao, zona)
}
