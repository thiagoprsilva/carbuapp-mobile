package br.com.carbuapp.auth.data

import br.com.carbuapp.auth.data.local.UserDao
import br.com.carbuapp.auth.data.local.UserEntity
import br.com.carbuapp.auth.domain.AuthRepository
import br.com.carbuapp.auth.domain.model.User
import br.com.carbuapp.core.data.TokenDataStore
import br.com.carbuapp.core.util.parseHttpError
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService,
    private val tokenDataStore: TokenDataStore,
    private val userDao: UserDao
) : AuthRepository {

    override suspend fun login(email: String, senha: String): Result<User> {
        return try {
            val response = apiService.login(LoginRequest(email, senha))
            tokenDataStore.saveToken(response.token)
            val user = response.user.toDomain()
            userDao.insertOrReplace(user.toEntity())
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(Exception(parseHttpError(e)))
        }
    }

    override suspend fun me(): Result<User> {
        return try {
            val user = apiService.me().toDomain()
            userDao.insertOrReplace(user.toEntity())
            Result.success(user)
        } catch (e: Exception) {
            // fallback para cache local se sem rede
            val cached = userDao.getCurrentUser()
            if (cached != null) Result.success(cached.toDomain())
            else Result.failure(Exception(parseHttpError(e)))
        }
    }

    override suspend fun logout() {
        tokenDataStore.clearAll()   // limpa token + oficina selecionada
        userDao.clearAll()
    }
}

// Mappers
private fun User.toEntity() = UserEntity(
    id = id,
    nome = nome,
    email = email,
    role = role,
    oficinaId = oficinaId
)

private fun UserEntity.toDomain() = User(
    id = id,
    nome = nome,
    email = email,
    role = role,
    oficinaId = oficinaId
)
