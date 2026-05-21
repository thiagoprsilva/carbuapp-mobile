package br.com.carbuapp.laudos.domain

import br.com.carbuapp.laudos.domain.model.Avaria
import br.com.carbuapp.laudos.domain.model.Laudo

data class LaudoSaveRequest(
    val osId: Int,
    val km: Int?,
    val nivelCombust: String?,
    val observacoes: String?,
    val avarias: List<AvariaInput>
)

data class AvariaInput(
    val zona: String,
    val severidade: String?,
    val observacao: String?
)

interface LaudoRepository {
    /** Retorna o laudo da OS (cache → remoto). Null se não existir. */
    suspend fun get(osId: Int): Laudo?

    /** Cria ou atualiza o laudo. */
    suspend fun save(request: LaudoSaveRequest): Laudo

    /** Remove o laudo localmente e na API. */
    suspend fun delete(osId: Int)
}
