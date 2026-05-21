package br.com.carbuapp.laudos.domain.model

data class Avaria(
    val id: Int,
    val zona: String,
    val severidade: String?,
    val observacao: String?
)

data class Laudo(
    val osId: Int,
    val km: Int?,
    val nivelCombust: String?,
    val observacoes: String?,
    val avarias: List<Avaria>
)

val NIVEIS_COMBUSTIVEL = listOf("1/4", "1/2", "3/4", "cheio")
val SEVERIDADES        = listOf("leve", "moderado", "grave")

// Zonas do veículo para mapeamento de avarias
val ZONAS_VEICULO = listOf(
    "Frente",
    "Traseira",
    "Lateral Esquerda",
    "Lateral Direita",
    "Teto",
    "Para-brisa",
    "Vidro Traseiro",
    "Capô",
    "Tampa Traseira",
    "Para-choque Dianteiro",
    "Para-choque Traseiro",
    "Interior"
)
