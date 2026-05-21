package br.com.carbuapp.templates.domain.usecase

import br.com.carbuapp.templates.domain.TemplateRepository
import javax.inject.Inject

class DeleteTemplateUseCase @Inject constructor(private val repo: TemplateRepository) {
    suspend operator fun invoke(id: Int) = repo.delete(id)
}
