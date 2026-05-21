package br.com.carbuapp.fotos.data

import br.com.carbuapp.fotos.data.local.FotoEntity
import br.com.carbuapp.fotos.domain.model.Foto

data class FotoDto(
    val id: Int,
    val url: String,
    val descricao: String?,
    val zona: String?,
    val criadoEm: String?,
    val registroTecnicoId: Int
)

fun FotoDto.toDomain(): Foto = Foto(
    id        = id,
    osId      = registroTecnicoId,
    url       = url,
    descricao = descricao,
    zona      = zona,
    criadoEm  = criadoEm
)

fun FotoDto.toEntity(): FotoEntity = FotoEntity(
    id        = id,
    osId      = registroTecnicoId,
    url       = url,
    descricao = descricao,
    zona      = zona,
    criadoEm  = criadoEm
)

fun FotoEntity.toDomain(): Foto = Foto(
    id        = id,
    osId      = osId,
    url       = url,
    descricao = descricao,
    zona      = zona,
    criadoEm  = criadoEm
)
