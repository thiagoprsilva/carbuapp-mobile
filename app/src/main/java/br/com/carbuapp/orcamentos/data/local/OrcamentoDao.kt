package br.com.carbuapp.orcamentos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OrcamentoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(orcamentos: List<OrcamentoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(orcamento: OrcamentoEntity)

    @Query("SELECT * FROM orcamentos ORDER BY numero DESC")
    fun observeAll(): Flow<List<OrcamentoEntity>>

    @Query("SELECT * FROM orcamentos WHERE status = :status ORDER BY numero DESC")
    fun observeByStatus(status: String): Flow<List<OrcamentoEntity>>

    @Query("SELECT * FROM orcamentos WHERE osId = :osId ORDER BY numero DESC")
    fun observeByOS(osId: Int): Flow<List<OrcamentoEntity>>

    @Query("SELECT * FROM orcamentos WHERE id = :id")
    suspend fun getById(id: Int): OrcamentoEntity?

    @Query("DELETE FROM orcamentos WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM orcamentos")
    suspend fun clearAll()
}
