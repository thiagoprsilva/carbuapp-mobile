package br.com.carbuapp.auth.data

import br.com.carbuapp.auth.domain.AuthRepository
import br.com.carbuapp.auth.domain.model.User
import br.com.carbuapp.core.data.TokenDataStore
import br.com.carbuapp.core.util.parseHttpError
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService,
    private val tokenDataStore: TokenDataStore
) : AuthRepository {

    override suspend fun login(email: String, senha: String): Result<User> {
        return try {
            val response = apiService.login(LoginRequest(email, senha))
            tokenDataStore.saveToken(response.token)
            Result.success(response.user.toDomain())
        } catch (e: Exception) {
            Result.failure(Exception(parseHttpError(e)))
        }
    }

    override suspend fun me(): Result<User> {
        return try {
            Result.success(apiService.me().toDomain())
        } catch (e: Exception) {
            Result.failure(Exception(parseHttpError(e)))
        }
    }

    override suspend fun logout() {
        tokenDataStore.clearToken()
    }
}
