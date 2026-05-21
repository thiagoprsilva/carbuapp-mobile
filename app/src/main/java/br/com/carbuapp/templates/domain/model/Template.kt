package br.com.carbuapp.templates.domain.model

data class TemplateItem(
    val id: Int,
    val descricao: String,
    val qtd: Int,
    val precoUnit: Double
)

data class Template(
    val id: Int,
    val nome: String,
    val createdAt: String?,
    val itens: List<TemplateItem>
)
