package com.jcarrasco96.une2021.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Registry(
    @PrimaryKey(autoGenerate = true) var id: Long,
    @ColumnInfo(name = "lectura") var lectura: Long,
    @ColumnInfo(name = "ultima_lectura") var ultima_lectura: Long,
    @ColumnInfo(name = "fecha") var fecha: String,
    @ColumnInfo(name = "oficial") var oficial: Boolean
)