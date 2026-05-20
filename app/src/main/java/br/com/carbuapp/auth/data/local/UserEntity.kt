package br.com.carbuapp.auth.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Int,
    val nome: String,
    val email: String,
    val role: String,         // "ADMIN" | "MECANICO" | "SUPERADMIN"
    val oficinaId: Int?,
    val updatedAt: Long = System.currentTimeMillis()
)
