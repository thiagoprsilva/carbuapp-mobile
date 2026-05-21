package br.com.carbuapp.fotos.domain

import android.net.Uri
import br.com.carbuapp.fotos.domain.model.Foto

interface FotoRepository {
    suspend fun list(osId: Int): List<Foto>
    suspend fun upload(osId: Int, uri: Uri, descricao: String? = null, zona: String? = null): Foto
    suspend fun delete(osId: Int, fotoId: Int)
}
