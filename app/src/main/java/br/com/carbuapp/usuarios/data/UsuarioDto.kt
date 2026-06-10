package br.com.carbuapp.usuarios.data

import br.com.carbuapp.usuarios.domain.Usuario

data class UsuarioDto(
    val id: Int,
    val nome: String,
    val email: String,
    val role: String,
    val ativo: Boolean,
    val oficinaId: Int?
)

fun UsuarioDto.toDomain() = Usuario(
    id        = id,
    nome      = nome,
    email     = email,
    role      = role,
    ativo     = ativo,
    oficinaId = oficinaId
)

data class UsuarioCreateRequest(
    val nome: String,
    val email: String,
    val senha: String,
    val role: String
)

data class UsuarioUpdateRequest(
    val nome: String,
    val email: String,
    val role: String,
    val ativo: Boolean
)

data class ResetSenhaRequest(val novaSenha: String)
