package br.com.carbuapp.templates.domain.usecase

import br.com.carbuapp.templates.domain.TemplateRepository
import br.com.carbuapp.templates.domain.model.Template
import javax.inject.Inject

class GetTemplatesUseCase @Inject constructor(private val repo: TemplateRepository) {
    suspend operator fun invoke(): List<Template> = repo.list()
}
