package br.com.carbuapp.clientes.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(clientes: List<ClienteEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(cliente: ClienteEntity)

    @Query("SELECT * FROM clientes ORDER BY nome ASC")
    fun observeAll(): Flow<List<ClienteEntity>>

    @Query("SELECT * FROM clientes ORDER BY nome ASC")
    suspend fun getAll(): List<ClienteEntity>

    @Query("SELECT * FROM clientes WHERE id = :id")
    suspend fun getById(id: Int): ClienteEntity?

    @Query("SELECT * FROM clientes WHERE nome LIKE '%' || :query || '%' ORDER BY nome ASC")
    fun search(query: String): Flow<List<ClienteEntity>>

    @Query("DELETE FROM clientes WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM clientes")
    suspend fun clearAll()
}
