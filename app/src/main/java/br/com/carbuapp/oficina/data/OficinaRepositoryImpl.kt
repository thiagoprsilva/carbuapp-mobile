package br.com.carbuapp.oficina.data

import android.content.Context
import android.net.Uri
import br.com.carbuapp.core.util.parseHttpError
import br.com.carbuapp.oficina.domain.Oficina
import br.com.carbuapp.oficina.domain.OficinaRepository
import br.com.carbuapp.oficina.domain.OficinaUpdateInput
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OficinaRepositoryImpl @Inject constructor(
    private val apiService: OficinaApiService,
    @ApplicationContext private val context: Context
) : OficinaRepository {

    override suspend fun listarTodas(): Result<List<Oficina>> {
        return try {
            Result.success(apiService.listarTodas().map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(Exception(parseHttpError(e)))
        }
    }

    override suspend fun getById(id: Int): Result<Oficina> {
        return try {
            Result.success(apiService.getById(id).toDomain())
        } catch (e: Exception) {
            Result.failure(Exception(parseHttpError(e)))
        }
    }

    override suspend fun update(id: Int, input: OficinaUpdateInput): Result<Oficina> {
        return try {
            val body = OficinaUpdateRequest(
                nome        = input.nome,
                responsavel = input.responsavel,
                telefone    = input.telefone,
                endereco    = input.endereco
            )
            Result.success(apiService.update(id, body).toDomain())
        } catch (e: Exception) {
            Result.failure(Exception(parseHttpError(e)))
        }
    }

    override suspend fun uploadLogo(id: Int, uri: Uri): Result<Oficina> {
        return try {
            val bytes = context.contentResolver.openInputStream(uri)?.readBytes()
                ?: return Result.failure(Exception("Não foi possível ler o arquivo"))
            val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
            val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("logo", "logo.jpg", requestBody)
            Result.success(apiService.uploadLogo(id, part).toDomain())
        } catch (e: Exception) {
            Result.failure(Exception(parseHttpError(e)))
        }
    }

    override suspend fun deleteLogo(id: Int): Result<Unit> {
        return try {
            apiService.deleteLogo(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(parseHttpError(e)))
        }
    }
}
