package br.com.carbuapp.templates.data

import br.com.carbuapp.templates.data.local.TemplateDao
import br.com.carbuapp.templates.domain.TemplateRepository
import br.com.carbuapp.templates.domain.TemplateSaveRequest
import br.com.carbuapp.templates.domain.model.Template
import javax.inject.Inject

class TemplateRepositoryImpl @Inject constructor(
    private val api: TemplateApiService,
    private val dao: TemplateDao
) : TemplateRepository {

    override suspend fun list(): List<Template> {
        return try {
            val dtos = api.list()
            dao.replaceAll(
                templates = dtos.map { it.toEntity() },
                itens     = dtos.flatMap { dto -> dto.itens.map { it.toEntity(dto.id) } }
            )
            dtos.map { it.toDomain() }
        } catch (e: Exception) {
            // Fallback para cache
            val entities = dao.getAll()
            entities.map { entity ->
                entity.toDomain(dao.getItens(entity.id))
            }
        }
    }

    override suspend fun save(request: TemplateSaveRequest): Template {
        val body = TemplateRequest(
            nome  = request.nome,
            itens = request.itens.map { TemplateItemRequest(it.descricao, it.qtd, it.precoUnit) }
        )
        val dto = if (request.id != null) {
            api.update(request.id, body)
        } else {
            api.create(body)
        }
        dao.upsert(dto.toEntity(), dto.itens.map { it.toEntity(dto.id) })
        return dto.toDomain()
    }

    override suspend fun delete(id: Int) {
        api.delete(id)
        dao.delete(id)
    }
}
