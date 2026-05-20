package br.com.carbuapp.dashboard.data

import br.com.carbuapp.core.util.parseHttpError
import br.com.carbuapp.dashboard.domain.DashboardSummary
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepositoryImpl @Inject constructor(
    private val apiService: DashboardApiService
) {
    suspend fun getSummary(): Result<DashboardSummary> {
        return try {
            Result.success(apiService.summary().toDomain())
        } catch (e: Exception) {
            Result.failure(Exception(parseHttpError(e)))
        }
    }
}
