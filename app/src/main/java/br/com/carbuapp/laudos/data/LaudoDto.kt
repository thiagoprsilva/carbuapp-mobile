package br.com.carbuapp.laudos.data

import br.com.carbuapp.laudos.data.local.AvariaEntity
import br.com.carbuapp.laudos.data.local.LaudoEntity
import br.com.carbuapp.laudos.domain.model.Avaria
import br.com.carbuapp.laudos.domain.model.Laudo

// ── Response ──────────────────────────────────────────────────────────────────

data class AvariaDto(
    val id: Int,
    val zona: String,
    val severidade: String?,
    val observacao: String?
)

data class LaudoResponseDto(
    val osId: Int,
    val km: Int?,
    val nivelCombust: String?,
    val observacoes: String?,
    val avarias: List<AvariaDto> = emptyList()
)

// ── Request ───────────────────────────────────────────────────────────────────

data class AvariaRequest(
    val zona: String,
    val severidade: String?,
    val observacao: String?
)

data class LaudoRequest(
    val km: Int?,
    val nivelCombust: String?,
    val observacoes: String?,
    val avarias: List<AvariaRequest>
)

// ── Mappers ───────────────────────────────────────────────────────────────────

fun LaudoResponseDto.toDomain(): Laudo = Laudo(
    osId       = osId,
    km         = km,
    nivelCombust = nivelCombust,
    observacoes  = observacoes,
    avarias    = avarias.map { it.toDomain(osId) }
)

fun AvariaDto.toDomain(osId: Int): Avaria = Avaria(
    id         = id,
    zona       = zona,
    severidade = severidade,
    observacao = observacao
)

fun LaudoResponseDto.toEntity(): LaudoEntity = LaudoEntity(
    osId         = osId,
    km           = km,
    nivelCombust = nivelCombust,
    observacoes  = observacoes
)

fun AvariaDto.toEntity(osId: Int): AvariaEntity = AvariaEntity(
    id         = id,
    osId       = osId,
    zona       = zona,
    severidade = severidade,
    observacao = observacao
)

fun LaudoEntity.toDomain(avarias: List<AvariaEntity>): Laudo = Laudo(
    osId         = osId,
    km           = km,
    nivelCombust = nivelCombust,
    observacoes  = observacoes,
    avarias      = avarias.map { it.toDomain() }
)

fun AvariaEntity.toDomain(): Avaria = Avaria(
    id         = id,
    zona       = zona,
    severidade = severidade,
    observacao = observacao
)
