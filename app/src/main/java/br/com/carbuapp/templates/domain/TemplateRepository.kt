package br.com.carbuapp.templates.domain

import br.com.carbuapp.templates.domain.model.Template

data class TemplateItemInput(
    val descricao: String,
    val qtd: Int,
    val precoUnit: Double
)

data class TemplateSaveRequest(
    val id: Int? = null,   // null = criar; non-null = atualizar
    val nome: String,
    val itens: List<TemplateItemInput>
)

interface TemplateRepository {
    suspend fun list(): List<Template>
    suspend fun save(request: TemplateSaveRequest): Template
    suspend fun delete(id: Int)
}
