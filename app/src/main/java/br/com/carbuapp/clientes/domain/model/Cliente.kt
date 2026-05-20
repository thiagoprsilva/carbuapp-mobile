package br.com.carbuapp.clientes.domain.model

data class Cliente(
    val id: Int,
    val nome: String,
    val telefone: String?,
    val createdAt: String
)
