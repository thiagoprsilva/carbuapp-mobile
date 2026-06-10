package br.com.carbuapp.orcamentos.domain.model

data class OrcamentoItem(
    val id: Int,
    val descricao: String,
    val qtd: Double,
    val precoUnit: Double,
    val valorLinha: Double
)

data class Orcamento(
    val id: Int,
    val numero: Int,
    val status: String,
    val subtotal: Double,
    val total: Double,
    val veiculoId: Int,
    val placa: String,
    val modelo: String,
    val clienteNome: String,
    val clienteId: Int,
    val clienteTelefone: String?,
    val osId: Int,
    val osNumero: Int,
    val createdAt: String
)

data class OrcamentoDetalhe(
    val orcamento: Orcamento,
    val itens: List<OrcamentoItem>
)

val STATUS_ORCAMENTO = listOf("Pendente", "Aprovado", "Rejeitado", "Executado")
