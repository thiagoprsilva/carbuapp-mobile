package br.com.carbuapp.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.carbuapp.auth.data.local.UserDao
import br.com.carbuapp.auth.data.local.UserEntity
import br.com.carbuapp.clientes.data.local.ClienteDao
import br.com.carbuapp.clientes.data.local.ClienteEntity
import br.com.carbuapp.fotos.data.local.FotoDao
import br.com.carbuapp.fotos.data.local.FotoEntity
import br.com.carbuapp.templates.data.local.TemplateDao
import br.com.carbuapp.templates.data.local.TemplateEntity
import br.com.carbuapp.templates.data.local.TemplateItemEntity
import br.com.carbuapp.laudos.data.local.AvariaEntity
import br.com.carbuapp.laudos.data.local.LaudoDao
import br.com.carbuapp.laudos.data.local.LaudoEntity
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
        OrcamentoEntity::class,
        LaudoEntity::class,
        AvariaEntity::class,
        FotoEntity::class,
        TemplateEntity::class,
        TemplateItemEntity::class
    ],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun clienteDao(): ClienteDao
    abstract fun veiculoDao(): VeiculoDao
    abstract fun osDao(): OSDao
    abstract fun orcamentoDao(): OrcamentoDao
    abstract fun laudoDao(): LaudoDao
    abstract fun fotoDao(): FotoDao
    abstract fun templateDao(): TemplateDao

    companion object {
        const val DATABASE_NAME = "carbuapp.db"
    }
}
