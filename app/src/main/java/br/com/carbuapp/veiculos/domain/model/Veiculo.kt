package br.com.carbuapp.veiculos.domain.model

data class Veiculo(
    val id: Int,
    val placa: String,
    val modelo: String,
    val ano: String?,
    val motor: String?,
    val alimentacao: String?,
    val clienteId: Int,
    val clienteNome: String?,
    val createdAt: String
)

// Evento de timeline (registro técnico ou orçamento)
sealed class TimelineEvento {
    abstract val id: Int
    abstract val data: String

    data class Registro(
        override val id: Int,
        override val data: String,
        val categoria: String,
        val descricao: String,
        val observacoes: String?
    ) : TimelineEvento()

    data class Orcamento(
        override val id: Int,
        override val data: String,
        val numero: Int,
        val total: Double
    ) : TimelineEvento()
}
