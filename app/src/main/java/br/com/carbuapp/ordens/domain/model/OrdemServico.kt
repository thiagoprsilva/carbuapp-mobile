package br.com.carbuapp.ordens.domain.model

data class OrdemServico(
    val id: Int,
    val numero: Int,
    val status: String,
    val categoria: String,
    val descricao: String,
    val dataServico: String,
    val observacoes: String?,
    val veiculoId: Int,
    val placa: String,
    val modelo: String,
    val clienteNome: String,
    val clienteId: Int
)

data class OrdemServicoDetalhe(
    val os: OrdemServico,
    val temLaudo: Boolean,
    val totalFotos: Int,
    val totalOrcamentos: Int
)

val STATUS_OS = listOf(
    "Aberta",
    "Em andamento",
    "Aguardando peças",
    "Concluída",
    "Cancelada"
)
