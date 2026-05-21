package br.com.carbuapp.templates.domain.usecase

import br.com.carbuapp.templates.domain.TemplateRepository
import br.com.carbuapp.templates.domain.TemplateSaveRequest
import br.com.carbuapp.templates.domain.model.Template
import javax.inject.Inject

class SaveTemplateUseCase @Inject constructor(private val repo: TemplateRepository) {
    suspend operator fun invoke(request: TemplateSaveRequest): Template = repo.save(request)
}
