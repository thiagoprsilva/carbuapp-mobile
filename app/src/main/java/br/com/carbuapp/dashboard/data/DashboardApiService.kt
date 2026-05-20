package br.com.carbuapp.dashboard.data

import retrofit2.http.GET

interface DashboardApiService {
    @GET("dashboard/summary")
    suspend fun summary(): DashboardSummaryDto
}
