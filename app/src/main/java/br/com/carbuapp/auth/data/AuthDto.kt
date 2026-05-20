package br.com.carbuapp.auth.data

import br.com.carbuapp.auth.domain.model.User

data class LoginRequest(
    val email: String,
    val senha: String
)

data class LoginResponse(
    val token: String,
    val user: UserDto
)

data class UserDto(
    val id: Int,
    val nome: String,
    val email: String,
    val role: String,
    val oficinaId: Int?
)

data class AlteraSenhaRequest(
    val senhaAtual: String,
    val novaSenha: String
)

data class MessageResponse(val message: String)

// Mapper: DTO → Domain
fun UserDto.toDomain() = User(
    id = id,
    nome = nome,
    email = email,
    role = role,
    oficinaId = oficinaId
)
