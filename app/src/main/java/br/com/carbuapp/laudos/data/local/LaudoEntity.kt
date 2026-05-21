package br.com.carbuapp.laudos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "laudos")
data class LaudoEntity(
    @PrimaryKey val osId: Int,          // 1:1 com a OS
    val km: Int?,
    val nivelCombust: String?,
    val observacoes: String?,
    val cachedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "avarias", primaryKeys = ["id"])
data class AvariaEntity(
    val id: Int,
    val osId: Int,                      // FK para LaudoEntity
    val zona: String,
    val severidade: String?,
    val observacao: String?
)
