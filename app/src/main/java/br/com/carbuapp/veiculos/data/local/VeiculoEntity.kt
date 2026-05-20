package br.com.carbuapp.veiculos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "veiculos")
data class VeiculoEntity(
    @PrimaryKey val id: Int,
    val placa: String,
    val modelo: String,
    val ano: String?,
    val motor: String?,
    val alimentacao: String?,
    val clienteId: Int,
    val clienteNome: String?,
    val createdAt: String,
    val cachedAt: Long = System.currentTimeMillis()
)
