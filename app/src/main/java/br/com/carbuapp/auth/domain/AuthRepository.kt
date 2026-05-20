package br.com.carbuapp.auth.domain

import br.com.carbuapp.auth.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, senha: String): Result<User>
    suspend fun me(): Result<User>
    suspend fun logout()
}
