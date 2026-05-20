package br.com.carbuapp.oficina.domain

interface OficinaRepository {
    suspend fun listarTodas(): Result<List<Oficina>>
}
