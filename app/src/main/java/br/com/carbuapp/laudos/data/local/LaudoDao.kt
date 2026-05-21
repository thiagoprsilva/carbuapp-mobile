package br.com.carbuapp.laudos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface LaudoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLaudo(laudo: LaudoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvarias(avarias: List<AvariaEntity>)

    @Query("SELECT * FROM laudos WHERE osId = :osId")
    suspend fun getLaudo(osId: Int): LaudoEntity?

    @Query("SELECT * FROM avarias WHERE osId = :osId")
    suspend fun getAvarias(osId: Int): List<AvariaEntity>

    @Query("DELETE FROM laudos WHERE osId = :osId")
    suspend fun deleteLaudo(osId: Int)

    @Query("DELETE FROM avarias WHERE osId = :osId")
    suspend fun deleteAvarias(osId: Int)

    @Transaction
    suspend fun upsert(laudo: LaudoEntity, avarias: List<AvariaEntity>) {
        deleteAvarias(laudo.osId)
        deleteLaudo(laudo.osId)
        insertLaudo(laudo)
        if (avarias.isNotEmpty()) insertAvarias(avarias)
    }

    @Transaction
    suspend fun delete(osId: Int) {
        deleteAvarias(osId)
        deleteLaudo(osId)
    }
}
