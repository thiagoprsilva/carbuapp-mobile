package br.com.carbuapp.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.carbuapp.auth.data.local.UserDao
import br.com.carbuapp.auth.data.local.UserEntity
import br.com.carbuapp.clientes.data.local.ClienteDao
import br.com.carbuapp.clientes.data.local.ClienteEntity
import br.com.carbuapp.orcamentos.data.local.OrcamentoDao
import br.com.carbuapp.orcamentos.data.local.OrcamentoEntity
import br.com.carbuapp.ordens.data.local.OSDao
import br.com.carbuapp.ordens.data.local.OSEntity
import br.com.carbuapp.veiculos.data.local.VeiculoDao
import br.com.carbuapp.veiculos.data.local.VeiculoEntity

@Database(
    entities = [
        UserEntity::class,
        ClienteEntity::class,
        VeiculoEntity::class,
        OSEntity::class,
        OrcamentoEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun clienteDao(): ClienteDao
    abstract fun veiculoDao(): VeiculoDao
    abstract fun osDao(): OSDao
    abstract fun orcamentoDao(): OrcamentoDao

    companion object {
        const val DATABASE_NAME = "carbuapp.db"
    }
}
