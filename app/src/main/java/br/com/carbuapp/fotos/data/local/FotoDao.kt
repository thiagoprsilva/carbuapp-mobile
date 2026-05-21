package br.com.carbuapp.fotos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(fotos: List<FotoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(foto: FotoEntity)

    @Query("SELECT * FROM fotos WHERE osId = :osId ORDER BY cachedAt DESC")
    suspend fun getByOs(osId: Int): List<FotoEntity>

    @Query("DELETE FROM fotos WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM fotos WHERE osId = :osId")
    suspend fun deleteByOs(osId: Int)
}
