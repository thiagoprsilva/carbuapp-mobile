package br.com.carbuapp.ordens.domain.usecase

import br.com.carbuapp.ordens.domain.OSRepository
import javax.inject.Inject

class DeleteOSUseCase @Inject constructor(
    private val repository: OSRepository
) {
    suspend operator fun invoke(id: Int): Result<Unit> = repository.delete(id)
}
