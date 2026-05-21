package br.com.carbuapp.core.di

import android.content.Context
import androidx.room.Room
import br.com.carbuapp.auth.data.local.UserDao
import br.com.carbuapp.core.database.AppDatabase
import br.com.carbuapp.fotos.data.local.FotoDao
import br.com.carbuapp.templates.data.local.TemplateDao
import br.com.carbuapp.laudos.data.local.LaudoDao
import br.com.carbuapp.orcamentos.data.local.OrcamentoDao
import br.com.carbuapp.ordens.data.local.OSDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
        .fallbackToDestructiveMigration()   // OK em dev; remover antes de produção
        .build()

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    fun provideOSDao(db: AppDatabase): OSDao = db.osDao()

    @Provides
    fun provideOrcamentoDao(db: AppDatabase): OrcamentoDao = db.orcamentoDao()

    @Provides
    fun provideLaudoDao(db: AppDatabase): LaudoDao = db.laudoDao()

    @Provides
    fun provideFotoDao(db: AppDatabase): FotoDao = db.fotoDao()

    @Provides
    fun provideTemplateDao(db: AppDatabase): TemplateDao = db.templateDao()
}
