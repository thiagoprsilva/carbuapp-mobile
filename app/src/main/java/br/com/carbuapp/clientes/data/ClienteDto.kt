package br.com.carbuapp.clientes.data

import br.com.carbuapp.clientes.data.local.ClienteEntity
import br.com.carbuapp.clientes.domain.model.Cliente

data class ClienteDto(
    val id: Int,
    val nome: String,
    val telefone: String?,
    val createdAt: String
)

data class ClienteRequest(
    val nome: String,
    val telefone: String?
)

// DTO → Domain
fun ClienteDto.toDomain() = Cliente(
    id = id,
    nome = nome,
    telefone = telefone,
    createdAt = createdAt
)

// DTO → Entity
fun ClienteDto.toEntity() = ClienteEntity(
    id = id,
    nome = nome,
    telefone = telefone,
    createdAt = createdAt
)

// Entity → Domain
fun ClienteEntity.toDomain() = Cliente(
    id = id,
    nome = nome,
    telefone = telefone,
    createdAt = createdAt
)

// Domain → Entity
fun Cliente.toEntity() = ClienteEntity(
    id = id,
    nome = nome,
    telefone = telefone,
    createdAt = createdAt
)
