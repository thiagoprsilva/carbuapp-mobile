package br.com.carbuapp.templates.data

import br.com.carbuapp.templates.data.local.TemplateEntity
import br.com.carbuapp.templates.data.local.TemplateItemEntity
import br.com.carbuapp.templates.domain.model.Template
import br.com.carbuapp.templates.domain.model.TemplateItem

// ── Response ──────────────────────────────────────────────────────────────────

data class TemplateItemDto(
    val id: Int,
    val descricao: String,
    val qtd: Int,
    val precoUnit: Double
)

data class TemplateDto(
    val id: Int,
    val nome: String,
    val createdAt: String?,
    val itens: List<TemplateItemDto> = emptyList()
)

// ── Request ───────────────────────────────────────────────────────────────────

data class TemplateItemRequest(
    val descricao: String,
    val qtd: Int,
    val precoUnit: Double
)

data class TemplateRequest(
    val nome: String,
    val itens: List<TemplateItemRequest>
)

// ── Mappers ───────────────────────────────────────────────────────────────────

fun TemplateDto.toDomain(): Template = Template(
    id        = id,
    nome      = nome,
    createdAt = createdAt,
    itens     = itens.map { it.toDomain() }
)

fun TemplateItemDto.toDomain(): TemplateItem = TemplateItem(
    id        = id,
    descricao = descricao,
    qtd       = qtd,
    precoUnit = precoUnit
)

fun TemplateDto.toEntity(): TemplateEntity = TemplateEntity(
    id        = id,
    nome      = nome,
    createdAt = createdAt
)

fun TemplateItemDto.toEntity(templateId: Int): TemplateItemEntity = TemplateItemEntity(
    id         = id,
    templateId = templateId,
    descricao  = descricao,
    qtd        = qtd,
    precoUnit  = precoUnit
)

fun TemplateEntity.toDomain(itens: List<TemplateItemEntity>): Template = Template(
    id        = id,
    nome      = nome,
    createdAt = createdAt,
    itens     = itens.map { it.toDomain() }
)

fun TemplateItemEntity.toDomain(): TemplateItem = TemplateItem(
    id        = id,
    descricao = descricao,
    qtd       = qtd,
    precoUnit = precoUnit
)
