package br.com.carbuapp.clientes.domain

import br.com.carbuapp.clientes.domain.model.Cliente
import kotlinx.coroutines.flow.Flow

interface ClienteRepository {
    fun observeAll(): Flow<List<Cliente>>
    suspend fun refresh(): Result<Unit>
    suspend fun getById(id: Int): Result<Cliente>
    suspend fun create(nome: String, telefone: String?): Result<Cliente>
    suspend fun update(id: Int, nome: String, telefone: String?): Result<Cliente>
    suspend fun delete(id: Int): Result<Unit>
    fun search(query: String): Flow<List<Cliente>>
}
