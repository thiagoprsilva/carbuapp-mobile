package br.com.carbuapp.fotos.data

import android.content.Context
import android.net.Uri
import br.com.carbuapp.fotos.data.local.FotoDao
import br.com.carbuapp.fotos.domain.FotoRepository
import br.com.carbuapp.fotos.domain.model.Foto
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class FotoRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: FotoApiService,
    private val dao: FotoDao
) : FotoRepository {

    override suspend fun list(osId: Int): List<Foto> {
        return try {
            val dtos = api.list(osId)
            dao.deleteByOs(osId)
            dao.insertAll(dtos.map { it.toEntity() })
            dtos.map { it.toDomain() }
        } catch (e: Exception) {
            // Fallback para cache
            dao.getByOs(osId).map { it.toDomain() }
        }
    }

    override suspend fun upload(osId: Int, uri: Uri, descricao: String?, zona: String?): Foto {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: error("Não foi possível abrir o arquivo.")

        // Copia para arquivo temporário para envio
        val tempFile = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
        tempFile.outputStream().use { out -> inputStream.copyTo(out) }

        val requestBody = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("foto", tempFile.name, requestBody)

        val descBody = descricao?.toRequestBody("text/plain".toMediaTypeOrNull())
        val zonaBody = zona?.toRequestBody("text/plain".toMediaTypeOrNull())

        val dto = api.upload(osId, part, descBody, zonaBody)
        dao.insert(dto.toEntity())
        tempFile.delete()
        return dto.toDomain()
    }

    override suspend fun delete(osId: Int, fotoId: Int) {
        api.delete(osId, fotoId)
        dao.deleteById(fotoId)
    }
}
