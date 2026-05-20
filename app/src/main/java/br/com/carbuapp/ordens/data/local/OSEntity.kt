package br.com.carbuapp.ordens.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ordens_servico")
data class OSEntity(
    @PrimaryKey val id: Int,
    val numero: Int,
    val status: String,
    val categoria: String,
    val descricao: String,
    val dataServico: String,
    val observacoes: String?,
    val veiculoId: Int,
    val placa: String,
    val modelo: String,
    val clienteNome: String,
    val clienteId: Int,
    val cachedAt: Long = System.currentTimeMillis()
)
