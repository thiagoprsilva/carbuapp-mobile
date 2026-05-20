package br.com.carbuapp.dashboard.domain

data class DashboardSummary(
    val totais: Totais,
    val recentes: Recentes
)

data class Totais(
    val clientes: Int,
    val veiculos: Int,
    val registros: Int,
    val orcamentos: Int
)

data class OSRecente(
    val id: Int,
    val numero: Int,
    val status: String,
    val categoria: String,
    val descricao: String,
    val dataServico: String,
    val placa: String,
    val modelo: String,
    val clienteNome: String
)

data class OrcamentoRecente(
    val id: Int,
    val numero: Int,
    val total: Double,
    val createdAt: String,
    val placa: String,
    val modelo: String,
    val clienteNome: String
)

data class Recentes(
    val registros: List<OSRecente>,
    val orcamentos: List<OrcamentoRecente>
)
