package com.jcarrasco96.une2021.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Contador(
    @PrimaryKey(autoGenerate = true) var id: Long,
    @ColumnInfo(name = "nombre") var nombre: String
)