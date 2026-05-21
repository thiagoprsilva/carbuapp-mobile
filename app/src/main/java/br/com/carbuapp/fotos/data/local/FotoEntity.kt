package br.com.carbuapp.fotos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fotos")
data class FotoEntity(
    @PrimaryKey val id: Int,
    val osId: Int,
    val url: String,
    val descricao: String?,
    val zona: String?,
    val criadoEm: String?,
    val cachedAt: Long = System.currentTimeMillis()
)
