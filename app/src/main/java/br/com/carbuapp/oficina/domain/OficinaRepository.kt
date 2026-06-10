package br.com.carbuapp.oficina.domain

import android.net.Uri

data class OficinaUpdateInput(
    val nome: String,
    val responsavel: String,
    val telefone: String,
    val endereco: String
)

interface OficinaRepository {
    suspend fun listarTodas(): Result<List<Oficina>>
    suspend fun getById(id: Int): Result<Oficina>
    suspend fun update(id: Int, input: OficinaUpdateInput): Result<Oficina>
    suspend fun uploadLogo(id: Int, uri: Uri): Result<Oficina>
    suspend fun deleteLogo(id: Int): Result<Unit>
}
