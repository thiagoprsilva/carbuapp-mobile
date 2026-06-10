package br.com.carbuapp.orcamentos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orcamentos")
data class OrcamentoEntity(
    @PrimaryKey val id: Int,
    val numero: Int,
    val status: String,
    val subtotal: Double,
    val total: Double,
    val veiculoId: Int,
    val placa: String,
    val modelo: String,
    val clienteNome: String,
    val clienteId: Int,
    val clienteTelefone: String? = null,
    val osId: Int,
    val osNumero: Int,
    val createdAt: String,
    val cachedAt: Long = System.currentTimeMillis()
)
