package br.com.carbuapp.veiculos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VeiculoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(veiculos: List<VeiculoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(veiculo: VeiculoEntity)

    @Query("SELECT * FROM veiculos ORDER BY modelo ASC")
    fun observeAll(): Flow<List<VeiculoEntity>>

    @Query("SELECT * FROM veiculos WHERE clienteId = :clienteId ORDER BY modelo ASC")
    fun observeByCliente(clienteId: Int): Flow<List<VeiculoEntity>>

    @Query("SELECT * FROM veiculos WHERE id = :id")
    suspend fun getById(id: Int): VeiculoEntity?

    @Query("SELECT * FROM veiculos WHERE placa LIKE '%' || :query || '%' OR modelo LIKE '%' || :query || '%' ORDER BY modelo ASC")
    fun search(query: String): Flow<List<VeiculoEntity>>

    @Query("DELETE FROM veiculos WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM veiculos")
    suspend fun clearAll()
}
