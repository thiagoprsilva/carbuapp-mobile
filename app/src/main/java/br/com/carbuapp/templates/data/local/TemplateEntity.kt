package br.com.carbuapp.templates.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "templates")
data class TemplateEntity(
    @PrimaryKey val id: Int,
    val nome: String,
    val createdAt: String?,
    val cachedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "template_itens", primaryKeys = ["id"])
data class TemplateItemEntity(
    val id: Int,
    val templateId: Int,
    val descricao: String,
    val qtd: Int,
    val precoUnit: Double
)
