package br.com.carbuapp.oficina.data

import br.com.carbuapp.core.util.parseHttpError
import br.com.carbuapp.oficina.domain.Oficina
import br.com.carbuapp.oficina.domain.OficinaRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OficinaRepositoryImpl @Inject constructor(
    private val apiService: OficinaApiService
) : OficinaRepository {

    override suspend fun listarTodas(): Result<List<Oficina>> {
        return try {
            Result.success(apiService.listarTodas().map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(Exception(parseHttpError(e)))
        }
    }
}
