package br.com.carbuapp.clientes.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clientes")
data class ClienteEntity(
    @PrimaryKey val id: Int,
    val nome: String,
    val telefone: String?,
    val createdAt: String,
    val cachedAt: Long = System.currentTimeMillis()
)
