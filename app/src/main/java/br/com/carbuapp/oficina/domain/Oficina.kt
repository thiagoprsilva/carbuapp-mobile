package br.com.carbuapp.oficina.domain

data class Oficina(
    val id: Int,
    val nome: String,
    val responsavel: String,
    val telefone: String,
    val endereco: String,
    val logoUrl: String?,
    val totalUsuarios: Int,
    val totalClientes: Int
)
