package br.com.carbuapp.core.network

import br.com.carbuapp.core.data.TokenDataStore
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenDataStore: TokenDataStore
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val (token, selectedOficinaId) = runBlocking {
            Pair(
                tokenDataStore.getToken(),
                tokenDataStore.getSelectedOficinaId()
            )
        }

        val request = chain.request().newBuilder()
            .apply {
                if (token != null) addHeader("Authorization", "Bearer $token")
                // Superadmin: informa ao backend qual oficina está acessando
                if (selectedOficinaId != null) addHeader("X-Oficina-Id", selectedOficinaId.toString())
            }
            .build()

        return chain.proceed(request)
    }
}
