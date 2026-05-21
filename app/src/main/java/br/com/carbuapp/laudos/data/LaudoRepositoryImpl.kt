package br.com.carbuapp.laudos.data

import br.com.carbuapp.laudos.data.local.LaudoDao
import br.com.carbuapp.laudos.domain.AvariaInput
import br.com.carbuapp.laudos.domain.LaudoRepository
import br.com.carbuapp.laudos.domain.LaudoSaveRequest
import br.com.carbuapp.laudos.domain.model.Laudo
import retrofit2.HttpException
import javax.inject.Inject

class LaudoRepositoryImpl @Inject constructor(
    private val api: LaudoApiService,
    private val dao: LaudoDao
) : LaudoRepository {

    override suspend fun get(osId: Int): Laudo? {
        return try {
            val dto = api.get(osId)
            // Atualiza cache
            dao.upsert(dto.toEntity(), dto.avarias.map { it.toEntity(osId) })
            dto.toDomain()
        } catch (e: HttpException) {
            if (e.code() == 404) {
                // Sem laudo — limpa cache antigo se houver
                dao.delete(osId)
                null
            } else {
                // Falha de rede — tenta retornar do cache
                val cached = dao.getLaudo(osId) ?: return null
                cached.toDomain(dao.getAvarias(osId))
            }
        } catch (e: Exception) {
            // Sem internet — retorna cache
            val cached = dao.getLaudo(osId) ?: return null
            cached.toDomain(dao.getAvarias(osId))
        }
    }

    override suspend fun save(request: LaudoSaveRequest): Laudo {
        val body = LaudoRequest(
            km           = request.km,
            nivelCombust = request.nivelCombust,
            observacoes  = request.observacoes,
            avarias      = request.avarias.map {
                AvariaRequest(zona = it.zona, severidade = it.severidade, observacao = it.observacao)
            }
        )
        val dto = api.save(request.osId, body)
        dao.upsert(dto.toEntity(), dto.avarias.map { it.toEntity(request.osId) })
        return dto.toDomain()
    }

    override suspend fun delete(osId: Int) {
        api.delete(osId)
        dao.delete(osId)
    }
}
