package br.com.carbuapp.templates.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface TemplateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: TemplateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItens(itens: List<TemplateItemEntity>)

    @Query("SELECT * FROM templates ORDER BY nome ASC")
    suspend fun getAll(): List<TemplateEntity>

    @Query("SELECT * FROM template_itens WHERE templateId = :templateId")
    suspend fun getItens(templateId: Int): List<TemplateItemEntity>

    @Query("DELETE FROM templates WHERE id = :id")
    suspend fun deleteTemplate(id: Int)

    @Query("DELETE FROM template_itens WHERE templateId = :templateId")
    suspend fun deleteItens(templateId: Int)

    @Query("DELETE FROM templates")
    suspend fun clearAll()

    @Query("DELETE FROM template_itens")
    suspend fun clearAllItens()

    @Transaction
    suspend fun upsert(template: TemplateEntity, itens: List<TemplateItemEntity>) {
        deleteItens(template.id)
        insertTemplate(template)
        if (itens.isNotEmpty()) insertItens(itens)
    }

    @Transaction
    suspend fun delete(id: Int) {
        deleteItens(id)
        deleteTemplate(id)
    }

    @Transaction
    suspend fun replaceAll(templates: List<TemplateEntity>, itens: List<TemplateItemEntity>) {
        clearAllItens()
        clearAll()
        templates.forEach { insertTemplate(it) }
        if (itens.isNotEmpty()) insertItens(itens)
    }
}
