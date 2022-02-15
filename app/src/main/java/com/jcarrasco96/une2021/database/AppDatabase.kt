package com.jcarrasco96.une2021.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Registry::class, Contador::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun registryDao(): RegistryDao
    abstract fun contadorDao(): ContadorDao

    companion object {

        private var INSTANCE: AppDatabase? = null

        fun appDatabase(context: Context): AppDatabase =
            INSTANCE ?: Room.databaseBuilder(context, AppDatabase::class.java, "une2021-v2.db")
                .allowMainThreadQueries().build().apply { INSTANCE = this }

    }

}