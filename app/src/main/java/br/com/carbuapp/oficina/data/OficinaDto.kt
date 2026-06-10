package br.com.carbuapp.oficina.data

import br.com.carbuapp.oficina.domain.Oficina

data class OficinaUpdateRequest(
    val nome: String,
    val responsavel: String,
    val telefone: String,
    val endereco: String
)

data class OficinaCountDto(
    val usuarios: Int,
    val clientes: Int
)

data class OficinaDto(
    val id: Int,
    val nome: String,
    val responsavel: String,
    val telefone: String,
    val endereco: String,
    val logoUrl: String?,
    val _count: OficinaCountDto?
)

fun OficinaDto.toDomain() = Oficina(
    id          = id,
    nome        = nome,
    responsavel = responsavel,
    telefone    = telefone,
    endereco    = endereco,
    logoUrl     = logoUrl,
    totalUsuarios = _count?.usuarios ?: 0,
    totalClientes = _count?.clientes ?: 0
)
