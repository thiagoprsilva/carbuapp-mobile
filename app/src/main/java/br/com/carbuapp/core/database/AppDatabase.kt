package br.com.carbuapp.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.carbuapp.auth.data.local.UserDao
import br.com.carbuapp.auth.data.local.UserEntity
import br.com.carbuapp.clientes.data.local.ClienteDao
import br.com.carbuapp.clientes.data.local.ClienteEntity
import br.com.carbuapp.veiculos.data.local.VeiculoDao
import br.com.carbuapp.veiculos.data.local.VeiculoEntity

@Database(
    entities = [
        UserEntity::class,
        ClienteEntity::class,
        VeiculoEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun clienteDao(): ClienteDao
    abstract fun veiculoDao(): VeiculoDao

    companion object {
        const val DATABASE_NAME = "carbuapp.db"
    }
}
