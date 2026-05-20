package br.com.carbuapp.ordens.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OSDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ordens: List<OSEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(os: OSEntity)

    @Query("SELECT * FROM ordens_servico ORDER BY numero DESC")
    fun observeAll(): Flow<List<OSEntity>>

    @Query("SELECT * FROM ordens_servico WHERE status = :status ORDER BY numero DESC")
    fun observeByStatus(status: String): Flow<List<OSEntity>>

    @Query("SELECT * FROM ordens_servico WHERE id = :id")
    suspend fun getById(id: Int): OSEntity?

    @Query("DELETE FROM ordens_servico WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM ordens_servico")
    suspend fun clearAll()
}
